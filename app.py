from flask import Flask, request, jsonify
from pyresparser import ResumeParser
import os

app = Flask(__name__)

@app.route('/parse', methods=['POST'])
def parse_resume():
    file = request.files['file']
    
    file_path = r"C:\Users\User\Desktop\AI interview Bot\AIInterviewBot\Adoni Meenakshi Naidu_JFSR Developer_3.2 Yrs.pdf"
    file.save(file_path)

    try:
        data = ResumeParser(file_path).get_extracted_data()
    except Exception as e:
        return jsonify({"error": str(e)}), 500
    finally:
        if os.path.exists(file_path):
            os.remove(file_path)

    return jsonify(data)

if __name__ == '__main__':
    app.run(port=5000)