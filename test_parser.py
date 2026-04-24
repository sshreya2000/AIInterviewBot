from pyresparser import ResumeParser

data = ResumeParser('Adoni Meenakshi Naidu_JFSR Developer_3.2 Yrs.pdf').get_extracted_data()

print(data)