## Resume Parser - Practical Examples

### Example 1: Parse Resume from Text

```java
@Service
@RequiredArgsConstructor
public class ResumeParsing {
    private final SkillExtractionService skillExtractionService;
    private final ResumeCorrector resumeCorrector;
    private final ObjectMapper objectMapper;
    
    public ParsedResume parseResume(String resumeText) {
        // Step 1: Extract using Gemini AI
        String rawResponse = skillExtractionService.extractFromResume(resumeText);
        
        // Step 2: Parse JSON
        JsonNode jsonNode = objectMapper.readTree(rawResponse);
        
        // Step 3: Correct and normalize
        ParsedResume corrected = resumeCorrector.correctFromJson(jsonNode);
        
        return corrected;
    }
}
```

---

### Example 2: Skill Matching with Job Description

```java
@Service
@RequiredArgsConstructor
public class SkillMatcher {
    private final SkillExtractionService skillExtractionService;
    private final ResumeCorrector resumeCorrector;
    private final ObjectMapper objectMapper;
    
    public SkillMatchResult matchResumeToJob(
            String resumeText, 
            String jobDescriptionText) {
        
        // Parse resume
        ParsedResume resume = parseResume(resumeText);
        
        // Parse job description
        String rawJd = skillExtractionService.extractFromJd(jobDescriptionText);
        JsonNode jdJson = objectMapper.readTree(rawJd);
        
        List<String> mandatorySkills = new ArrayList<>();
        List<String> optionalSkills = new ArrayList<>();
        
        jdJson.path("mandatorySkills").forEach(n -> 
            mandatorySkills.add(n.asText())
        );
        jdJson.path("optionalSkills").forEach(n -> 
            optionalSkills.add(n.asText())
        );
        
        // Calculate match
        List<String> candidateSkills = resume.getAllSkills();
        
        List<String> matchedMandatory = mandatorySkills.stream()
            .filter(skill -> candidateSkills.stream()
                .anyMatch(cs -> cs.equalsIgnoreCase(skill)))
            .collect(Collectors.toList());
        
        List<String> matchedOptional = optionalSkills.stream()
            .filter(skill -> candidateSkills.stream()
                .anyMatch(cs -> cs.equalsIgnoreCase(skill)))
            .collect(Collectors.toList());
        
        int matchPercentage = (int) (
            (matchedMandatory.size() * 100) / 
            Math.max(mandatorySkills.size(), 1)
        );
        
        return SkillMatchResult.builder()
            .candidateName(resume.getCandidateName())
            .matchPercentage(matchPercentage)
            .matchedMandatorySkills(matchedMandatory)
            .matchedOptionalSkills(matchedOptional)
            .missingMandatorySkills(
                mandatorySkills.stream()
                    .filter(s -> !matchedMandatory.contains(s))
                    .collect(Collectors.toList())
            )
            .candidateSkills(candidateSkills)
            .build();
    }
    
    private ParsedResume parseResume(String resumeText) {
        String rawResponse = skillExtractionService.extractFromResume(resumeText);
        JsonNode json = objectMapper.readTree(rawResponse);
        return resumeCorrector.correctFromJson(json);
    }
}
```

---

### Example 3: Candidate Database Enrichment

```java
@Service
@RequiredArgsConstructor
public class CandidateService {
    private final ResumeCorrector resumeCorrector;
    private final CandidateRepository repository;
    
    public void enrichCandidateWithResume(
            String candidateId, 
            String resumeText) {
        
        // Parse resume
        // ... extraction code ...
        ParsedResume corrected = resumeCorrector.correct(parsed);
        
        // Store in database
        Candidate candidate = repository.findById(candidateId).orElseThrow();
        
        candidate.setSkills(corrected.getSkills());
        candidate.setInferredSkills(corrected.getInferredSkills());
        candidate.setExperience(corrected.getYearsOfExperience());
        candidate.setProjects(corrected.getProjects());
        candidate.setEducation(corrected.getEducation());
        
        repository.save(candidate);
    }
}
```

---

### Example 4: Resume Comparison

```java
@Service
@RequiredArgsConstructor
public class ResumeComparison {
    private final ResumeCorrector resumeCorrector;
    
    public ComparisonResult compareResumes(ParsedResume resume1, ParsedResume resume2) {
        ParsedResume c1 = resumeCorrector.correct(resume1);
        ParsedResume c2 = resumeCorrector.correct(resume2);
        
        Set<String> skills1 = new HashSet<>(c1.getAllSkills());
        Set<String> skills2 = new HashSet<>(c2.getAllSkills());
        
        Set<String> commonSkills = new HashSet<>(skills1);
        commonSkills.retainAll(skills2);
        
        Set<String> uniqueToR1 = new HashSet<>(skills1);
        uniqueToR1.removeAll(skills2);
        
        Set<String> uniqueToR2 = new HashSet<>(skills2);
        uniqueToR2.removeAll(skills1);
        
        return ComparisonResult.builder()
            .candidate1(c1.getCandidateName())
            .candidate2(c2.getCandidateName())
            .commonSkills(new ArrayList<>(commonSkills))
            .uniqueToCandidate1(new ArrayList<>(uniqueToR1))
            .uniqueToCandidate2(new ArrayList<>(uniqueToR2))
            .overallSimilarity(
                (commonSkills.size() * 100) / 
                Math.max(skills1.size(), skills2.size())
            )
            .build();
    }
}
```

---

### Example 5: Resume Validation

```java
@Service
@RequiredArgsConstructor
public class ResumeValidator {
    private final ResumeCorrector resumeCorrector;
    
    public ValidationResult validate(ParsedResume resume) {
        ParsedResume corrected = resumeCorrector.correct(resume);
        List<String> warnings = new ArrayList<>();
        
        // Check for required fields
        if (corrected.getCandidateName() == null || 
            corrected.getCandidateName().isEmpty()) {
            warnings.add("Missing candidate name");
        }
        
        if (corrected.getSkills().isEmpty()) {
            warnings.add("No skills provided");
        }
        
        if (corrected.getYearsOfExperience() < 0) {
            warnings.add("Invalid experience years");
        }
        
        if (corrected.getEducation().isEmpty()) {
            warnings.add("No education information");
        }
        
        return ValidationResult.builder()
            .isValid(warnings.isEmpty())
            .warnings(warnings)
            .correctedResume(corrected)
            .build();
    }
}
```

---

### Example 6: Skill Gap Analysis

```java
@Service
@RequiredArgsConstructor
public class SkillGapAnalyzer {
    private final ResumeCorrector resumeCorrector;
    
    public SkillGapAnalysis analyzeGap(
            ParsedResume resume, 
            List<String> requiredSkills) {
        
        ParsedResume corrected = resumeCorrector.correct(resume);
        Set<String> candidateSkills = new HashSet<>(corrected.getAllSkills());
        
        List<String> acquiredSkills = requiredSkills.stream()
            .filter(skill -> candidateSkills.stream()
                .anyMatch(cs -> cs.equalsIgnoreCase(skill)))
            .collect(Collectors.toList());
        
        List<String> missingSkills = requiredSkills.stream()
            .filter(skill -> !acquiredSkills.contains(skill))
            .collect(Collectors.toList());
        
        return SkillGapAnalysis.builder()
            .candidateName(corrected.getCandidateName())
            .acquiredSkills(acquiredSkills)
            .missingSkills(missingSkills)
            .coveragePercentage(
                (acquiredSkills.size() * 100) / requiredSkills.size()
            )
            .build();
    }
}
```

---

### Example 7: Batch Resume Processing

```java
@Service
@RequiredArgsConstructor
public class BatchResumeProcessor {
    private final SkillExtractionService skillExtractionService;
    private final ResumeCorrector resumeCorrector;
    private final ObjectMapper objectMapper;
    private final CandidateRepository repository;
    
    public BatchResult processBatch(List<String> resumeTexts) {
        List<ParsedResume> results = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        
        for (int i = 0; i < resumeTexts.size(); i++) {
            try {
                String rawResponse = skillExtractionService
                    .extractFromResume(resumeTexts.get(i));
                
                JsonNode json = objectMapper.readTree(rawResponse);
                ParsedResume corrected = resumeCorrector.correctFromJson(json);
                
                results.add(corrected);
            } catch (Exception e) {
                errors.add("Resume " + i + ": " + e.getMessage());
            }
        }
        
        return BatchResult.builder()
            .processed(results.size())
            .failed(errors.size())
            .results(results)
            .errors(errors)
            .build();
    }
}
```

---

## Key Points

1. **Always use resumeCorrector.correct()** after parsing
2. **Use getAllSkills()** for complete skill list
3. **JSON methods** for API integration
4. **Import ParsedResume** to your services

## Quick Integration Checklist

- [ ] Added `@Autowired private ResumeCorrector resumeCorrector;`
- [ ] Call `resumeCorrector.correct(parsed)` after parsing
- [ ] Use `corrected.getSkills()` for normalized skills
- [ ] Use `corrected.getAllSkills()` for complete skill list
- [ ] Use `correctFromJson()` when parsing JSON responses
- [ ] Use `correctToJson()` for API responses

---

**Ready to use in your services! 🚀**

