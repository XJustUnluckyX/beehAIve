from flask import Flask, jsonify, request
import json

app = Flask(__name__)

@app.route("/predict",methods=["GET", "POST"])
def predict_CNN():
    if request.method == "POST":
        decoded_data = request.data.decode("utf-8")
        input = json.loads(decoded_data)
        return jsonify ({"sum": input["x"] + input["y"]})

if __name__ == "__main__":
    app.run()