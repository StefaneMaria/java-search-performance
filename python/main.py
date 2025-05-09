from flask import Flask, request, jsonify, send_file
import pandas as pd
import matplotlib

matplotlib.use("Agg")
import matplotlib.pyplot as plt
import seaborn as sns
import io
import os
import numpy as np
from datetime import datetime
import traceback
from flask_cors import CORS

from analysis_functions import write_analysis_summary_to_md
import generate_graphics as g


app = Flask(__name__)
CORS(app)

# Criar diretório para salvar os gráficos temporários
UPLOAD_FOLDER = "temp_graphs"
if not os.path.exists(UPLOAD_FOLDER):
    os.makedirs(UPLOAD_FOLDER)


@app.route("/upload", methods=["POST"])
def upload_data():
    """
    Endpoint para fazer upload do arquivo CSV
    """
    global df

    if "file" not in request.files:
        return jsonify({"error": "Nenhum arquivo enviado"}), 400

    file = request.files["file"]
    if file.filename == "":
        return jsonify({"error": "Arquivo sem nome"}), 400

    try:
        df = pd.read_csv(file)
        return (
            jsonify({"message": "Arquivo carregado com sucesso", "rows": len(df)}),
            200,
        )
    except Exception as e:
        return jsonify({"error": f"Erro ao processar arquivo: {str(e)}"}), 500


@app.route("/analysis", methods=["GET"])
def get_analysis():
    """
    Endpoint para obter os resultados da análise
    """
    global df

    if df is None:
        return jsonify({"error": "Nenhum dado carregado"}), 400

    try:
        results = write_analysis_summary_to_md(df)
        return results, 200, {"Content-Type": "text/markdown; charset=utf-8"}
    except Exception as e:
        return jsonify({"error": f"Erro na análise: {str(e)}"}), 500


@app.route("/plots/<plot_type>", methods=["GET"])
def get_plot(plot_type):
    """
    Endpoint para obter os graficos
    """
    global df

    if df is None:
        return jsonify({"error": "Nenhum dado carregado"}), 400

    try:
        if plot_type == "performance_array_type":
            fig = g.plot_performance_by_array_type(df)
        elif plot_type == "size_impact":
            fig = g.plot_size_impact(df)
        elif plot_type == "parallel_efficiency":
            fig = g.plot_parallel_efficiency(df)
        elif plot_type == "speedup_vs_threads":
            fig = g.plot_speedup_vs_threads(df)
        elif plot_type == "parallelization_overhead":
            fig = g.plot_parallelization_overhead(df)
        elif plot_type == "efficiency_heatmap":
            fig = g.plot_efficiency_heatmap(df)
        elif plot_type == "execution_time_variability":
            fig = g.plot_execution_time_variability(df)
        elif plot_type == "size_vs_max_speedup":
            fig = g.plot_size_vs_max_speedup(df)
        else:
            return (
                jsonify({"error": f"Tipo de gráfico '{plot_type}' não reconhecido"}),
                400,
            )
        return fig.to_html(full_html=False, include_plotlyjs="cdn"), 200

    except Exception as e:
        return jsonify({"error": f"Erro ao gerar gráfico: {str(e)}"}), 500


if __name__ == "__main__":
    app.run(debug=True, host="0.0.0.0", port=5000)
