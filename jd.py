import spacy
from sklearn.feature_extraction.text import TfidfVectorizer

nlp = spacy.load("en_core_web_sm")

def extract_skills_from_jd(text):
    skills = set()

    doc = nlp(text)

    # 1. Noun phrases (Spring Boot, Microservices)
    for chunk in doc.noun_chunks:
        val = chunk.text.strip()
        if len(val) > 2:
            skills.add(val)

    # 2. Proper nouns (Java, AWS)
    for token in doc:
        if token.pos_ in ["PROPN"]:
            skills.add(token.text)

    # 3. TF-IDF keywords
    vectorizer = TfidfVectorizer(stop_words='english')
    X = vectorizer.fit_transform([text])
    keywords = vectorizer.get_feature_names_out()

    for word in keywords:
        if len(word) > 2:
            skills.add(word)

    # 4. Basic cleanup
    ignore = {"developer", "experience", "knowledge", "looking", "role"}

    cleaned = []
    for s in skills:
        s_lower = s.lower()
        if s_lower not in ignore:
            cleaned.append(s.strip())

    return list(set(cleaned))

from pdfminer.high_level import extract_text

def read_pdf(file_path):
    return extract_text(file_path)

jd_text = read_pdf("jd.pdf")   # or read_txt("jd.txt")

skills = extract_skills_from_jd(jd_text)

print(skills)