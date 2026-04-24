# Step-by-Step Execution Guide

## 🎯 What You Need to Do Now

Everything has been implemented. Follow these steps to verify and deploy:

---

## Phase 1: Compilation & Verification (5 minutes)

### Step 1: Navigate to backend directory
```powershell
cd "C:\Users\User\Desktop\AI interview Bot\AIInterviewBot\backend"
```

### Step 2: Clean and compile
```powershell
mvn clean compile
```

**Expected Output**:
```
[INFO] --- maven-compiler-plugin:3.x.x:compile (default-compile) @ ai-interview-backend ---
[INFO] Compiling 50 source files to ...
[INFO] BUILD SUCCESS
```

**If you get an error**:
- Check Java version: `java -version` (should be 21+)
- Check Maven: `mvn -version` (should be 3.6.0+)
- If still failing, file might not have saved - check using IDE

### Step 3: Full build with tests skipped
```powershell
mvn clean install -DskipTests
```

**Expected Output**:
```
[INFO] Building ai-interview-backend 0.0.1-SNAPSHOT
[INFO] ...
[INFO] BUILD SUCCESS
```

**What it does**:
- Cleans target directory
- Compiles all source code
- Creates JAR file
- Prepares for execution

---

## Phase 2: Application Startup (2 minutes)

### Step 4: Start the application
```powershell
mvn spring-boot:run
```

**Expected Output** (last few lines):
```
2026-04-23 10:15:32.123  INFO 12345 --- [main] c.c.a.AiInterviewApplication            : Started AiInterviewApplication in 15.234 seconds (process running for 15.567s)
2026-04-23 10:15:32.456  INFO 12345 --- [main] c.c.a.a.service.GeminiRateLimiter       : GeminiRateLimiter initialized: 60 requests/minute (~1000ms interval)
```

**Key Log to Look For**:
```
GeminiRateLimiter initialized: 60 requests/minute (~1000ms interval)
```

This confirms rate limiter is active ✅

### Step 5: Keep this terminal open
The application is now running. Open a **NEW** terminal for testing.

---

## Phase 3: Testing Rate Limiting (3 minutes)

### In a NEW terminal, generate a question

```powershell
# Example API call to test
curl -X POST http://localhost:8080/api/v1/ai/question `
  -H "Content-Type: application/json" `
  -d '{
    "jobDescription": "Java Developer",
    "difficulty": "Medium",
    "experienceLevel": "3 years"
  }'
```

**Expected Response**: JSON with question ✅

**Check the running terminal** for these logs:
```
Rate limiting: sleeping for 0ms       ← First request (no delay)
Making Gemini API request
Successfully generated content
```

### Make another request immediately
```powershell
curl -X POST http://localhost:8080/api/v1/ai/question `
  -H "Content-Type: application/json" `
  -d '{
    "jobDescription": "Java Developer",
    "difficulty": "Medium",
    "experienceLevel": "3 years"
  }'
```

**Check the logs** for:
```
Rate limiting: sleeping for 1000ms    ← Second request (1 second delay)
Making Gemini API request
Successfully generated content
```

This confirms rate limiting is working ✅

---

## Phase 4: Testing Caching (2 minutes)

### Make the SAME request a third time
```powershell
curl -X POST http://localhost:8080/api/v1/ai/question `
  -H "Content-Type: application/json" `
  -d '{
    "jobDescription": "Java Developer",
    "difficulty": "Medium",
    "experienceLevel": "3 years"
  }'
```

**Check the logs**:
- You should **NOT** see "Making Gemini API request"
- Response should be instant
- This means cache hit ✅

**Expected logs for cache hit**:
```
Rate limiting: sleeping for 1000ms
Executing Gemini API request
[Return from cache - no API call log]
```

---

## Phase 5: Testing Error Handling (Optional)

### Test with invalid API key or network issue
The logs should show:
```
Rate limited by Gemini API (429). Attempt 1/3
Retrying after 1000ms backoff
Rate limited by Gemini API (429). Attempt 2/3
Retrying after 2000ms backoff
Rate limited by Gemini API (429). Attempt 3/3
Max retries (3) exceeded for rate limit
```

This confirms retry logic works ✅

---

## Complete Testing Checklist

```
✅ Phase 1: Compilation
   ✓ mvn clean compile - SUCCESS
   ✓ mvn clean install - SUCCESS

✅ Phase 2: Application Startup
   ✓ mvn spring-boot:run - STARTED
   ✓ See "GeminiRateLimiter initialized" message

✅ Phase 3: Rate Limiting
   ✓ First request → immediate
   ✓ Second request → 1 second delayed
   ✓ Logs show "Rate limiting: sleeping for 1000ms"

✅ Phase 4: Caching
   ✓ Make same request twice
   ✓ Second request → instant (cache hit)
   ✓ No "Making Gemini API request" in logs

✅ Phase 5: Error Handling (Optional)
   ✓ If 429 occurs → auto-retries
   ✓ Shows exponential backoff: 1s, 2s, 4s
```

---

## What Each Change Does (Quick Reference)

### Rate Limiting (GeminiRateLimiter.java)
```
Request 1 → Immediate (0ms)
Request 2 → Wait 1000ms
Request 3 → Wait 1000ms
...
Result: 60 requests per minute max (1 per second)
```

### Retry Logic (GeminiClient.java)
```
API Call → 429 Error
  ↓
Wait 1 second → Retry (Attempt 1)
  ↓
If 429 Again: Wait 2 seconds → Retry (Attempt 2)
  ↓
If 429 Again: Wait 4 seconds → Retry (Attempt 3)
  ↓
If Still Failing: Return Error
```

### Caching (Service Classes)
```
First Call: generateQuestion(Java, Medium, 3yr)
  ↓ Generates question via API → Stores in cache

Second Call: generateQuestion(Java, Medium, 3yr)
  ↓ Cache key matches → Returns cached response instantly
```

---

## Troubleshooting During Testing

### "GeminiRateLimiter not initialized" message?
- **Solution**: Application didn't start fully
- **Action**: Wait 30 seconds for full startup
- **Check**: Look for other startup errors in logs

### "No logs appearing"?
- **Solution**: Logging might not be configured
- **Action**: Generate a request and wait 2 seconds for logs
- **Check**: Ensure you're looking at the correct terminal

### "Getting actual 429 errors"?
- **Solution**: Rate limit too high for your API tier
- **Action**: 
  1. Stop application (Ctrl+C)
  2. Edit `application.yml`
  3. Change `requests-per-minute: 60` to `requests-per-minute: 30`
  4. Restart: `mvn spring-boot:run`

### "Cache not returning from same request"?
- **Solution**: Need to use EXACTLY same parameters
- **Action**: Copy-paste the exact same curl command
- **Check**: Check for any differences in parameters

---

## Configuration Tuning (If Needed)

If you're on a **PAID API tier** with higher limits:

Edit `backend/src/main/resources/application.yml`:

```yaml
gemini:
  base-url: https://generativelanguage.googleapis.com
  api-key: ${GEMINI_API_KEY}
  model: gemini-2.0-flash
  max-retries: 3
  initial-backoff-ms: 1000
  requests-per-minute: 120  # ← Change from 60 to 120 for paid tier
```

Then:
```powershell
mvn clean install -DskipTests
mvn spring-boot:run
```

---

## Log Levels Explained

| Log Level | When You See It | Meaning |
|-----------|----------------|---------|
| DEBUG | "Rate limiting: sleeping" | Rate limit enforcing |
| DEBUG | "Making Gemini API request" | Calling Gemini API |
| DEBUG | "Executing Gemini API request" | About to make call |
| INFO | "GeminiRateLimiter initialized" | Rate limiter activated |
| WARN | "Rate limited by Gemini API (429)" | Got 429, will retry |
| ERROR | "Max retries exceeded" | Gave up after retries |

---

## Performance Expectations

| Action | Before | After | Status |
|--------|--------|-------|--------|
| First request | Instant | Instant | ✅ Same |
| Second request | Instant | 1 second delayed | 🟡 Slower (expected) |
| Same question twice | 2 API calls | 1 API call | ✅ Better |
| Rate limited | Crashes | Auto-retries | ✅ Better |

---

## Monitoring in Production

Keep these log messages in mind for production monitoring:

```bash
# Everything is working fine:
[INFO] GeminiRateLimiter initialized: 60 requests/minute
[DEBUG] Rate limiting: sleeping for Xms
[DEBUG] Successfully generated content from Gemini API

# Getting rate limited (normal - will retry):
[WARN] Rate limited by Gemini API (429). Attempt 1/3
[WARN] Retrying after 1000ms backoff

# Something is wrong:
[ERROR] Max retries (3) exceeded for rate limit
[ERROR] Gemini API call failed
[ERROR] Failed to generate content
```

---

## Emergency Rollback (If Needed)

If everything breaks and you need to rollback:

```powershell
# Stop the application
# Press Ctrl+C in the running terminal

# Revert the 6 modified files
# You can find originals in git history or backup

# Revert application.yml changes
# Remove the 3 new properties

# Rebuild
mvn clean install -DskipTests

# Restart
mvn spring-boot:run
```

All changes are backward compatible, so rollback is safe.

---

## Success Indicators

✅ **All Good** if you see:
- Application starts without errors
- "GeminiRateLimiter initialized" message
- Questions generate successfully
- Rate limiting delays visible in logs
- Cache hits reduce API calls
- No 429 errors (or auto-retries if they occur)

❌ **Problem** if you see:
- Compilation errors
- Application won't start
- "GeminiRateLimiter not found" exceptions
- 429 errors without retries
- Cache not working

---

## Final Command Sequence

Copy and paste these in order:

```powershell
# Terminal 1: Build and run
cd "C:\Users\User\Desktop\AI interview Bot\AIInterviewBot\backend"
mvn clean install -DskipTests
mvn spring-boot:run
```

```powershell
# Terminal 2: Test (after you see app is running)
# Wait for "GeminiRateLimiter initialized" message in Terminal 1

# Test 1: First request
curl -X POST http://localhost:8080/api/v1/ai/question `
  -H "Content-Type: application/json" `
  -d '{"jobDescription":"Java Developer","difficulty":"Medium","experienceLevel":"3 years"}'

# Test 2: Second request immediately
curl -X POST http://localhost:8080/api/v1/ai/question `
  -H "Content-Type: application/json" `
  -d '{"jobDescription":"Java Developer","difficulty":"Medium","experienceLevel":"3 years"}'

# Test 3: Third request (same as second) - should be cached
curl -X POST http://localhost:8080/api/v1/ai/question `
  -H "Content-Type: application/json" `
  -d '{"jobDescription":"Java Developer","difficulty":"Medium","experienceLevel":"3 years"}'
```

---

**You're all set! Follow the steps above to verify everything works. 🚀**

