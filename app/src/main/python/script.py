import json, os, shutil, io, base64
import pandas as pd
import matplotlib.pyplot as plt
from sklearn.tree import DecisionTreeClassifier
from sklearn import tree



def load_and_clean_from_json(json_path):
    with open(json_path, "r", encoding="utf-8") as f:
        data = json.load(f)

    rows = []
    for cat in data["cat"]:
        age = cat["age"]
        name = cat["name"]
        for game in cat["gameList"]:
            errors = game.get("errors", 0)
            game_time = game.get("gameTime", 0)
            points = game.get("points", 0)
            reaction = game.get("reactionTime", [])
            avg_reaction = sum(reaction)/len(reaction) if reaction else -1
            dificulty = game.get("dificulty", "unknown")

            rows.append({
                "age": age,
                "errors": errors,
                "gameTime": game_time,
                "points": points,
                "avgReaction": avg_reaction,
                "name": name,
                "dificulty": dificulty
            })

    df = pd.DataFrame(rows)
    df = df[df["gameTime"] > 0]
    df.drop_duplicates(inplace=True)
    return df

def fig_to_base64():
    buf = io.BytesIO()
    plt.savefig(buf, format="png")
    plt.close()
    buf.seek(0)
    return base64.b64encode(buf.read()).decode("utf-8")

def train():
    json_path = "dataset.json"
    df = load_and_clean_from_json(json_path)

    # Entrenar modelo (igual que antes)
    X = df[["age", "errors", "gameTime", "avgReaction"]]
    y = (df["points"] > 0).astype(int)
    clf = DecisionTreeClassifier(max_depth=10, random_state=42)
    clf.fit(X, y)
    global model
    model = clf
    return df, clf

    

def returnGraphs():
    df, clf = train()

    graphs = {}
    #points per player
    plt.figure(figsize=(24,16), dpi=100)
    df.groupby("name")["points"].mean().plot(kind="bar", color="orange")
    plt.title("Promedio de puntos por jugador")
    plt.ylabel("Puntos promedio")
    graphs["points_per_player"] = fig_to_base64()

    #game time per points
    plt.figure(figsize=(24,16), dpi=100)
    plt.scatter(df["gameTime"], df["points"], color="purple")
    plt.title("Relación tiempo de juego vs puntos")
    plt.xlabel("Tiempo de juego")
    plt.ylabel("Puntos")
    graphs["scatter_gameTime_points"] = fig_to_base64()

    #tree
    plt.figure(figsize=(24,16), dpi=120)
    tree.plot_tree(
    clf,
    feature_names=["age", "errors", "gameTime", "avgReaction"],
    class_names=["sin puntos", "con puntos"],
    filled=True,
    rounded=True,
    fontsize=12,
    proportion=False   # mejor para ver todos los nodos
    )
    plt.title("Árbol de decisión entrenado", fontsize=16, fontweight="bold")
    graphs["decision_tree"] = fig_to_base64()



    return graphs