import pandas as pd


def generate_analysis_summaries(df):
    """
    Gera resumos analíticos sobre o desempenho dos algoritmos de ordenação
    """
    results = {}

    # 1. Algoritmo mais rápido para cada tipo de array
    fastest_by_array_type = {}
    array_types = df["ArrayType"].unique()

    for array_type in array_types:
        serial_data = df[(df["Type"] == "Serial") & (df["ArrayType"] == array_type)]
        if not serial_data.empty:
            avg_times = serial_data.groupby("Algorithm")["TimeMs"].mean()
            fastest_algo = avg_times.idxmin()
            fastest_time = avg_times.min()

            other_algos_mean = avg_times[avg_times.index != fastest_algo].mean()
            speedup_vs_others = (
                other_algos_mean / fastest_time if fastest_time > 0 else 0
            )

            fastest_by_array_type[array_type] = {
                "fastest_algorithm": fastest_algo,
                "avg_time_ms": fastest_time,
                "times_faster_than_avg": speedup_vs_others,
            }

    results["fastest_by_array_type"] = fastest_by_array_type

    # 2. Algoritmo mais rápido para cada tamanho de array
    fastest_by_size = {}
    sizes = df["Size"].unique()

    for size in sizes:
        serial_data = df[(df["Type"] == "Serial") & (df["Size"] == size)]
        if not serial_data.empty:
            avg_times = serial_data.groupby("Algorithm")["TimeMs"].mean()
            fastest_algo = avg_times.idxmin()
            fastest_time = avg_times.min()

            slowest_time = avg_times.max()
            speedup_vs_slowest = slowest_time / fastest_time if fastest_time > 0 else 0

            fastest_by_size[size] = {
                "fastest_algorithm": fastest_algo,
                "avg_time_ms": fastest_time,
                "times_faster_than_slowest": speedup_vs_slowest,
            }

    results["fastest_by_size"] = fastest_by_size

    # 3. Algoritmo que mais se beneficia do paralelismo
    parallel_benefit = {}
    algorithms = df["Algorithm"].unique()

    for algo in algorithms:
        serial_data = df[(df["Type"] == "Serial") & (df["Algorithm"] == algo)]
        parallel_data = df[(df["Type"] == "Parallel") & (df["Algorithm"] == algo)]

        if not serial_data.empty and not parallel_data.empty:
            serial_avg = (
                serial_data.groupby(["Size", "ArrayType"])["TimeMs"]
                .mean()
                .reset_index()
            )
            serial_avg.rename(columns={"TimeMs": "SerialTime"}, inplace=True)

            parallel_best = (
                parallel_data.groupby(["Size", "ArrayType", "ThreadCount"])["TimeMs"]
                .mean()
                .reset_index()
            )
            parallel_min = parallel_best.loc[
                parallel_best.groupby(["Size", "ArrayType"])["TimeMs"].idxmin()
            ]
            parallel_min.rename(
                columns={
                    "TimeMs": "BestParallelTime",
                    "ThreadCount": "BestThreadCount",
                },
                inplace=True,
            )

            merged = pd.merge(
                serial_avg,
                parallel_min[
                    ["Size", "ArrayType", "BestParallelTime", "BestThreadCount"]
                ],
                on=["Size", "ArrayType"],
            )

            merged["SpeedUp"] = merged["SerialTime"] / merged["BestParallelTime"]

            best_speedup_row = merged.loc[merged["SpeedUp"].idxmax()]

            parallel_benefit[algo] = {
                "max_speedup": best_speedup_row["SpeedUp"],
                "best_thread_count": best_speedup_row["BestThreadCount"],
                "array_size": best_speedup_row["Size"],
                "array_type": best_speedup_row["ArrayType"],
                "serial_time_ms": best_speedup_row["SerialTime"],
                "parallel_time_ms": best_speedup_row["BestParallelTime"],
            }

    parallel_benefit = {
        k: v
        for k, v in sorted(
            parallel_benefit.items(),
            key=lambda item: item[1]["max_speedup"],
            reverse=True,
        )
    }
    results["parallel_benefit"] = parallel_benefit

    # 4. Melhor caso para cada algoritmo (tipo de array mais favorável)
    best_case_by_algo = {}

    for algo in algorithms:
        serial_data = df[(df["Type"] == "Serial") & (df["Algorithm"] == algo)]

        if not serial_data.empty:
            avg_times = (
                serial_data.groupby(["ArrayType", "Size"])["TimeMs"]
                .mean()
                .reset_index()
            )

            best_case = avg_times.loc[avg_times["TimeMs"].idxmin()]

            worst_case = avg_times.loc[avg_times["TimeMs"].idxmax()]
            difference_factor = (
                worst_case["TimeMs"] / best_case["TimeMs"]
                if best_case["TimeMs"] > 0
                else 0
            )

            best_case_by_algo[algo] = {
                "best_array_type": best_case["ArrayType"],
                "best_size": best_case["Size"],
                "best_time_ms": best_case["TimeMs"],
                "worst_array_type": worst_case["ArrayType"],
                "worst_size": worst_case["Size"],
                "worst_time_ms": worst_case["TimeMs"],
                "times_faster_than_worst": difference_factor,
            }

    results["best_case_by_algo"] = best_case_by_algo

    # 5. Eficiência de paralelização para diferentes números de threads
    thread_efficiency = {}
    thread_counts = sorted(df[df["Type"] == "Parallel"]["ThreadCount"].unique())

    for threads in thread_counts:
        thread_data = df[(df["Type"] == "Parallel") & (df["ThreadCount"] == threads)]

        if not thread_data.empty:
            efficiency = thread_data.groupby("Algorithm")["Efficiency"].mean().to_dict()
            thread_efficiency[threads] = efficiency

    results["thread_efficiency"] = thread_efficiency

    # 6. Análise de escalabilidade com o aumento de threads
    scalability = {}

    for algo in algorithms:
        parallel_data = df[(df["Type"] == "Parallel") & (df["Algorithm"] == algo)]

        if not parallel_data.empty:
            avg_speedup = (
                parallel_data.groupby("ThreadCount")["SpeedUp"].mean().to_dict()
            )

            thread_counts = sorted(avg_speedup.keys())
            speedup_increase = {}

            for i in range(len(thread_counts) - 1):
                current = thread_counts[i]
                next_thread = thread_counts[i + 1]

                if next_thread / current == 2:
                    increase_pct = (
                        avg_speedup[next_thread] / avg_speedup[current] - 1
                    ) * 100
                    speedup_increase[f"{current}_to_{next_thread}"] = increase_pct

            scalability[algo] = {
                "avg_speedup_by_threads": avg_speedup,
                "speedup_increase_pct": speedup_increase,
            }

    results["scalability"] = scalability

    # 7. Overhead da paralelização para algoritmos
    overhead_analysis = {}

    for algo in algorithms:
        serial_data = df[(df["Type"] == "Serial") & (df["Algorithm"] == algo)]
        parallel_2 = df[
            (df["Type"] == "Parallel")
            & (df["Algorithm"] == algo)
            & (df["ThreadCount"] == 2)
        ]

        if not serial_data.empty and not parallel_2.empty:
            serial_avg = serial_data["TimeMs"].mean()
            parallel_avg = parallel_2["TimeMs"].mean()
            overhead = parallel_avg / serial_avg if serial_avg > 0 else 0

            overhead_analysis[algo] = {
                "serial_avg_time_ms": serial_avg,
                "parallel_2_avg_time_ms": parallel_avg,
                "overhead_factor": overhead,
                "has_overhead": overhead > 1,
            }

    results["overhead_analysis"] = overhead_analysis

    # 8. Tamanho do array a partir do qual o paralelismo compensa
    parallelism_threshold = {}

    for algo in algorithms:
        sizes_data = {}

        for size in sizes:
            serial = df[
                (df["Type"] == "Serial")
                & (df["Algorithm"] == algo)
                & (df["Size"] == size)
            ]
            parallel = df[
                (df["Type"] == "Parallel")
                & (df["Algorithm"] == algo)
                & (df["Size"] == size)
            ]

            if not serial.empty and not parallel.empty:
                serial_avg = serial["TimeMs"].mean()

                any_parallel_better = False
                best_thread_count = None
                best_speedup = 0

                for thread in thread_counts:
                    thread_data = parallel[parallel["ThreadCount"] == thread]
                    if not thread_data.empty:
                        parallel_avg = thread_data["TimeMs"].mean()
                        speedup = serial_avg / parallel_avg if parallel_avg > 0 else 0

                        if speedup > 1 and speedup > best_speedup:
                            any_parallel_better = True
                            best_thread_count = thread
                            best_speedup = speedup

                sizes_data[size] = {
                    "parallelism_beneficial": any_parallel_better,
                    "best_thread_count": best_thread_count,
                    "best_speedup": best_speedup,
                }

        beneficial_sizes = [
            size for size, data in sizes_data.items() if data["parallelism_beneficial"]
        ]

        if beneficial_sizes:
            min_size = min(beneficial_sizes)
            parallelism_threshold[algo] = {
                "min_beneficial_size": min_size,
                "best_thread_count": sizes_data[min_size]["best_thread_count"],
                "speedup": sizes_data[min_size]["best_speedup"],
            }
        else:
            parallelism_threshold[algo] = {
                "min_beneficial_size": None,
                "best_thread_count": None,
                "speedup": 0,
                "note": "Paralelismo não compensa para nenhum tamanho testado",
            }

    results["parallelism_threshold"] = parallelism_threshold

    return results


def create_algorithm_thread_tables(df):
    """
    Cria tabelas cruzadas de algoritmos x threads para diferentes métricas
    """
    tables = {}

    # Tabela 1: Tempo médio de execução (algoritmo x threads)
    tables["avg_execution_time"] = create_metric_table(
        df, "TimeMs", "Tempo Médio de Execução (ms)"
    )

    # Tabela 2: Speedup médio (algoritmo x threads)
    tables["avg_speedup"] = create_metric_table(df, "SpeedUp", "Speedup Médio")

    # Tabela 3: Efficiency média (algoritmo x threads)
    tables["avg_efficiency"] = create_metric_table(df, "Efficiency", "Eficiência Média")

    # Tabela 4: Menor tempo de execução (algoritmo x threads)
    tables["min_execution_time"] = create_metric_table(
        df, "TimeMs", "Menor Tempo de Execução (ms)", aggfunc="min"
    )

    # Tabela 5: Maior tempo de execução (algoritmo x threads)
    tables["max_execution_time"] = create_metric_table(
        df, "TimeMs", "Maior Tempo de Execução (ms)", aggfunc="max"
    )

    return tables


def create_metric_table(df, metric_col, title, aggfunc="mean"):
    """
    Cria uma tabela cruzada para uma métrica específica
    """
    serial_df = df[df["Type"] == "Serial"].copy()
    parallel_df = df[df["Type"] == "Parallel"].copy()

    if not serial_df.empty:
        serial_metrics = serial_df.groupby("Algorithm")[metric_col].agg(aggfunc)
    else:
        serial_metrics = pd.Series(dtype=float)

    if not parallel_df.empty:
        # Pivot table for parallel data
        parallel_table = pd.pivot_table(
            parallel_df,
            values=metric_col,
            index="Algorithm",
            columns="ThreadCount",
            aggfunc=aggfunc,
        )

        parallel_table = parallel_table.rename(
            columns={col: f"{col} threads" for col in parallel_table.columns}
        )
    else:
        parallel_table = pd.DataFrame()

    if not serial_metrics.empty:
        if not parallel_table.empty:
            parallel_table["Serial"] = serial_metrics
            cols = ["Serial"] + [
                col for col in parallel_table.columns if col != "Serial"
            ]
            result_table = parallel_table[cols]
        else:
            result_table = pd.DataFrame({"Serial": serial_metrics})
    else:
        result_table = parallel_table

    # Formatando valores
    if metric_col == "TimeMs":
        result_table = result_table.round(4)
    elif metric_col in ["SpeedUp", "Efficiency"]:
        result_table = result_table.round(2)

    return result_table


def write_analysis_summary_to_md(df, filename="analysis_summary.md"):
    """
    Escreve os resultados da análise em um arquivo Markdown.
    """

    results = generate_analysis_summaries(df)
    tables = create_algorithm_thread_tables(df)

    with open(filename, "w", encoding="utf-8") as f:
        f.write("# Análise de Algoritmos de Ordenação\n\n")

        f.write("## 1. Tabelas de Análise de Algoritmos de Ordenação\n\n")

        # Tabela 1: Tempo médio de execução
        f.write("## Tempo Médio de Execução (ms)\n")
        f.write(convert_df_to_markdown(tables["avg_execution_time"]))
        f.write("\n\n")

        # Tabela 2: Speedup médio
        f.write("## Speedup Médio\n")
        f.write(convert_df_to_markdown(tables["avg_speedup"]))
        f.write("\n\n")

        # Tabela 3: Efficiency média
        f.write("## Eficiência Média\n")
        f.write(convert_df_to_markdown(tables["avg_efficiency"]))
        f.write("\n\n")

        # Tabela 4: Menor tempo de execução
        f.write("## Menor Tempo de Execução (ms)\n")
        f.write(convert_df_to_markdown(tables["min_execution_time"]))
        f.write("\n\n")

        # Tabela 5: Maior tempo de execução
        f.write("## Maior Tempo de Execução (ms)\n")
        f.write(convert_df_to_markdown(tables["max_execution_time"]))
        f.write("\n\n")

        f.write("## 2. Algoritmo mais rápido para cada tipo de array (Serial)\n")
        for array_type, data in results["fastest_by_array_type"].items():
            f.write(f"### Tipo de Array: {array_type}\n")
            f.write(f"- **Algoritmo mais rápido**: {data['fastest_algorithm']}\n")
            f.write(f"- **Tempo médio**: {data['avg_time_ms']:.4f} ms\n")
            f.write(
                f"- **Vezes mais rápido que a média dos outros algoritmos**: {data['times_faster_than_avg']:.2f}x\n\n"
            )

        f.write("## 2. Algoritmo mais rápido para cada tamanho de array (Serial)\n")
        for size, data in results["fastest_by_size"].items():
            f.write(f"### Tamanho: {size}\n")
            f.write(f"- **Algoritmo mais rápido**: {data['fastest_algorithm']}\n")
            f.write(f"- **Tempo médio**: {data['avg_time_ms']:.4f} ms\n")
            f.write(
                f"- **Vezes mais rápido que o algoritmo mais lento**: {data['times_faster_than_slowest']:.2f}x\n\n"
            )

        f.write("## 3. Algoritmos ordenados por benefício do paralelismo\n")
        for i, (algo, data) in enumerate(results["parallel_benefit"].items(), 1):
            f.write(f"### {i}. {algo}\n")
            f.write(f"- **Speedup máximo**: {data['max_speedup']:.2f}x\n")
            f.write(f"- **Melhor número de threads**: {data['best_thread_count']}\n")
            f.write(f"- **Tamanho do array**: {data['array_size']}\n")
            f.write(f"- **Tipo do array**: {data['array_type']}\n")
            f.write(
                f"- **Tempo serial**: {data['serial_time_ms']:.4f} ms → **Tempo paralelo**: {data['parallel_time_ms']:.4f} ms\n\n"
            )

        f.write("## 4. Melhor caso para cada algoritmo\n")
        for algo, data in results["best_case_by_algo"].items():
            f.write(f"### Algoritmo: {algo}\n")
            f.write(
                f"- **Melhor caso**: {data['best_array_type']} (tamanho {data['best_size']})\n"
            )
            f.write(f"- **Tempo**: {data['best_time_ms']:.4f} ms\n")
            f.write(
                f"- **Pior caso**: {data['worst_array_type']} (tamanho {data['worst_size']})\n"
            )
            f.write(f"- **Tempo do pior caso**: {data['worst_time_ms']:.4f} ms\n")
            f.write(
                f"- **O melhor caso é {data['times_faster_than_worst']:.2f}x mais rápido que o pior**\n\n"
            )

        f.write("## 5. Eficiência de paralelização por número de threads\n")
        for threads, algos in results["thread_efficiency"].items():
            f.write(f"### Número de threads: {threads}\n")
            sorted_algos = {
                k: v
                for k, v in sorted(
                    algos.items(), key=lambda item: item[1], reverse=True
                )
            }
            for algo, efficiency in sorted_algos.items():
                f.write(f"- **{algo}**: {efficiency:.4f} ({efficiency*100:.1f}%)\n")
            f.write("\n")

        f.write("## 6. Análise de escalabilidade com aumento de threads\n")
        for algo, data in results["scalability"].items():
            f.write(f"### Algoritmo: {algo}\n")
            f.write("- **Speedup médio por número de threads**:\n")
            for threads, speedup in data["avg_speedup_by_threads"].items():
                f.write(f"  - {threads} threads: {speedup:.2f}x\n")
            if data["speedup_increase_pct"]:
                f.write(
                    "- **Aumento percentual do speedup ao dobrar o número de threads**:\n"
                )
                for threads_range, increase in data["speedup_increase_pct"].items():
                    threads_from, threads_to = threads_range.split("_to_")
                    f.write(
                        f"  - {threads_from} → {threads_to} threads: {increase:.1f}% de aumento\n"
                    )
            f.write("\n")

        f.write("## 7. Análise de overhead da paralelização\n")
        overhead_sorted = {
            k: v
            for k, v in sorted(
                results["overhead_analysis"].items(),
                key=lambda item: item[1]["overhead_factor"],
                reverse=True,
            )
        }
        for algo, data in overhead_sorted.items():
            f.write(f"### Algoritmo: {algo}\n")
            f.write(f"- **Tempo serial médio**: {data['serial_avg_time_ms']:.4f} ms\n")
            f.write(
                f"- **Tempo paralelo (2 threads) médio**: {data['parallel_2_avg_time_ms']:.4f} ms\n"
            )
            f.write(f"- **Fator de overhead**: {data['overhead_factor']:.2f}\n")
            if data["has_overhead"]:
                f.write(
                    "- **ALERTA**: Este algoritmo tem overhead de paralelização significativo!\n"
                )
            f.write("\n")

        f.write("## 8. Tamanho do array a partir do qual o paralelismo compensa\n")
        for algo, data in results["parallelism_threshold"].items():
            f.write(f"### Algoritmo: {algo}\n")
            if data["min_beneficial_size"] is not None:
                f.write(f"- **Tamanho mínimo**: {data['min_beneficial_size']}\n")
                f.write(
                    f"- **Melhor número de threads**: {data['best_thread_count']}\n"
                )
                f.write(f"- **Speedup**: {data['speedup']:.2f}x\n")
            else:
                f.write(f"- {data['note']}\n")
            f.write("\n")

    with open(filename, "r", encoding="utf-8") as f:
        markdown_content = f.read()
    print(f"Análise salva em {filename}")
    return markdown_content


def convert_df_to_markdown(df):
    """
    Converte um DataFrame para tabela Markdown formatada
    """
    markdown = df.to_markdown()
    return markdown
