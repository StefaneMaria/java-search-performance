import pandas as pd
import plotly.express as px
import plotly.graph_objects as go


def plot_performance_by_array_type(df):
    avg_time = (
        df.groupby(["Algorithm", "ArrayType", "Type"])["TimeMs"].mean().reset_index()
    )

    # Criando o gráfico
    fig = px.bar(
        avg_time,
        x="Algorithm",
        y="TimeMs",
        color="ArrayType",
        facet_col="Type",  # Separar serial e paralelo
        title="Tempo médio de execução por algoritmo e tipo de array",
        labels={
            "TimeMs": "Tempo (ms)",
            "Algorithm": "Algoritmo",
            "ArrayType": "Tipo de Array",
        },
        barmode="group",
    )

    fig.update_layout(
        legend_title_text="Tipo de Array", font=dict(size=12), height=600, width=1000
    )

    return fig


def plot_size_impact(df):
    avg_time_by_size = (
        df.groupby(["Size", "Algorithm", "Type"])["TimeMs"].mean().reset_index()
    )

    fig = px.line(
        avg_time_by_size,
        x="Size",
        y="TimeMs",
        color="Algorithm",
        line_dash="Type",
        markers=True,
        title="Impacto do tamanho do array no tempo de execução",
        labels={
            "TimeMs": "Tempo (ms)",
            "Size": "Tamanho do Array",
            "Algorithm": "Algoritmo",
        },
        log_y=True,
    )

    fig.update_layout(
        legend_title_text="Algoritmo", font=dict(size=12), height=600, width=1000
    )

    return fig


def plot_parallel_efficiency(df):
    parallel_data = df[df["Type"] == "Parallel"]
    avg_efficiency = (
        parallel_data.groupby("Algorithm")["Efficiency"].mean().reset_index()
    )

    fig = px.bar(
        avg_efficiency,
        x="Algorithm",
        y="Efficiency",
        color="Algorithm",
        title="Eficiência média de paralelização por algoritmo",
        labels={"Efficiency": "Eficiência", "Algorithm": "Algoritmo"},
    )

    fig.update_layout(
        legend_title_text="Algoritmo", font=dict(size=12), height=600, width=1000
    )

    return fig


def plot_speedup_vs_threads(df):
    parallel_data = df[df["Type"] == "Parallel"]

    avg_speedup = (
        parallel_data.groupby(["Algorithm", "ArrayType", "ThreadCount"])["SpeedUp"]
        .mean()
        .reset_index()
    )

    fig = px.line(
        avg_speedup,
        x="ThreadCount",
        y="SpeedUp",
        color="Algorithm",
        facet_col="ArrayType",
        facet_col_wrap=2,
        markers=True,
        title="SpeedUp médio vs número de threads por tipo de array",
        labels={
            "SpeedUp": "SpeedUp",
            "ThreadCount": "Número de Threads",
            "Algorithm": "Algoritmo",
        },
    )

    for i in range(len(fig.data)):
        fig.add_trace(
            go.Scatter(
                x=[1, max(avg_speedup["ThreadCount"])],
                y=[1, max(avg_speedup["ThreadCount"])],
                mode="lines",
                name="Speedup Ideal",
                line=dict(color="black", dash="dash"),
                showlegend=(i == 0),
            )
        )

    fig.update_layout(
        legend_title_text="Algoritmo", font=dict(size=12), height=800, width=1200
    )

    return fig


def plot_parallelization_overhead(df):
    serial = (
        df[df["Type"] == "Serial"]
        .groupby(["Algorithm", "Size"])["TimeMs"]
        .mean()
        .reset_index()
    )
    parallel_2 = (
        df[(df["Type"] == "Parallel") & (df["ThreadCount"] == 2)]
        .groupby(["Algorithm", "Size"])["TimeMs"]
        .mean()
        .reset_index()
    )

    serial.rename(columns={"TimeMs": "SerialTime"}, inplace=True)
    parallel_2.rename(columns={"TimeMs": "Parallel2Time"}, inplace=True)

    merged = pd.merge(serial, parallel_2, on=["Algorithm", "Size"])

    merged["Overhead"] = merged["Parallel2Time"] / merged["SerialTime"]

    fig = px.bar(
        merged,
        x="Algorithm",
        y="Overhead",
        color="Size",
        barmode="group",
        title="Overhead de paralelização (Tempo com 2 threads / Tempo serial)",
        labels={
            "Overhead": "Overhead",
            "Algorithm": "Algoritmo",
            "Size": "Tamanho do Array",
        },
    )

    fig.add_hline(
        y=1, line_dash="dash", line_color="red", annotation_text="Sem overhead"
    )

    fig.update_layout(
        legend_title_text="Tamanho do Array", font=dict(size=12), height=600, width=1000
    )

    return fig


def plot_efficiency_heatmap(df):
    parallel_data = df[df["Type"] == "Parallel"]

    avg_efficiency = (
        parallel_data.groupby(["Algorithm", "ThreadCount"])["Efficiency"]
        .mean()
        .reset_index()
    )

    pivot_table = avg_efficiency.pivot(
        index="Algorithm", columns="ThreadCount", values="Efficiency"
    )

    fig = go.Figure(
        data=go.Heatmap(
            z=pivot_table.values,
            x=pivot_table.columns,
            y=pivot_table.index,
            colorscale="Viridis",
            colorbar=dict(title="Eficiência"),
        )
    )

    fig.update_layout(
        title="Eficiência de paralelização por algoritmo e número de threads",
        xaxis_title="Número de Threads",
        yaxis_title="Algoritmo",
        height=500,
        width=900,
    )

    return fig


def plot_execution_time_variability(df):
    time_stats = (
        df.groupby(["Algorithm", "Type", "Size", "ArrayType"])["TimeMs"]
        .agg(["mean", "std"])
        .reset_index()
    )

    time_stats["cv"] = time_stats["std"] / (time_stats["mean"] + 1e-10)

    fig = px.bar(
        time_stats,
        x="Algorithm",
        y="cv",
        color="ArrayType",
        facet_col="Type",
        facet_row="Size",
        title="Variabilidade do tempo de execução (Coeficiente de Variação)",
        labels={
            "cv": "Coeficiente de Variação",
            "Algorithm": "Algoritmo",
            "ArrayType": "Tipo de Array",
        },
    )

    fig.update_layout(height=800, width=1200)

    return fig


def plot_size_vs_max_speedup(df):
    parallel_data = df[df["Type"] == "Parallel"]
    max_speedup = (
        parallel_data.groupby(["Algorithm", "Size"])["SpeedUp"].max().reset_index()
    )

    fig = px.scatter(
        max_speedup,
        x="Size",
        y="SpeedUp",
        color="Algorithm",
        size="SpeedUp",
        title="Tamanho do array vs. SpeedUp máximo por algoritmo",
        labels={
            "SpeedUp": "SpeedUp Máximo",
            "Size": "Tamanho do Array",
            "Algorithm": "Algoritmo",
        },
    )

    fig = px.scatter(
        max_speedup,
        x="Size",
        y="SpeedUp",
        color="Algorithm",
        size="SpeedUp",
        trendline="ols",
        title="Tamanho do array vs. SpeedUp máximo por algoritmo",
        labels={
            "SpeedUp": "SpeedUp Máximo",
            "Size": "Tamanho do Array",
            "Algorithm": "Algoritmo",
        },
    )

    fig.update_layout(height=600, width=1000)

    return fig
