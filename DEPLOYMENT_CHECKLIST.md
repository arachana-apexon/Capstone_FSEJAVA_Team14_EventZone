# Render Deployment Checklist

## ✅ Pre-Deployment Checklist

### 1. Code Repository
- [ ] Code is pushed to GitHub, GitLab, or Bitbucket
- [ ] All deployment files are committed:
  - [ ] `Dockerfile`
  - [ ] `.dockerignore`
  - [ ] `render.yaml`
  - [ ] `application-prod.properties`
  - [ ] `DEPLOYMENT.md`
  - [ ] `build.sh` (optional)

### 2. Configuration Files Review
- [ ] Update `render.yaml` with your preferred region
- [ ] Set correct branch name in `render.yaml` (default: main)
- [ ] Review database plan (free or paid)
- [ ] Review web service plan (free or paid)

### 3. Security Configuration
- [ ] Generate a strong JWT secret for production
- [ ] Review CORS settings for your frontend URL
- [ ] Ensure no hardcoded passwords in code
- [ ] Verify database credentials use environment variables

### 4. Application Configuration
- [ ] Spring Boot Actuator added to `pom.xml` ✓
- [ ] Health check endpoint configured ✓
- [ ] Production profile created ✓
- [ ] Logging levels appropriate for production ✓

## 🚀 Deployment Steps

### Option 1: Blueprint Deployment (Recommended)

1. **Push to Repository**
   ```bash
   git add .
   git commit -m "Add Render deployment configuration"
   git push origin main
   ```

2. **Create Blueprint in Render**
   - [ ] Login to [Render Dashboard](https://dashboard.render.com/)
   - [ ] Click "New" → "Blueprint"
   - [ ] Connect your Git repository
   - [ ] Authorize Render to access your repository
   - [ ] Select repository and branch
   - [ ] Review the `render.yaml` configuration
   - [ ] Click "Apply" to create services

3. **Wait for Deployment**
   - [ ] Database provisioning (2-5 minutes)
   - [ ] Application build (5-10 minutes)
   - [ ] Initial deployment (2-5 minutes)

4. **Post-Deployment Configuration**
   - [ ] Update environment variables:
     - [ ] `ALLOWED_ORIGINS` - Add your frontend URL
     - [ ] `JWT_SECRET` - Add a secure random string (or use auto-generated)
     - [ ] Other custom variables as needed

### Option 2: Manual Deployment

#### Step 1: Create Database
- [ ] Navigate to Render Dashboard
- [ ] Click "New" → "PostgreSQL"
- [ ] Name: `eventzone-db`
- [ ] Database: `eventzone`
- [ ] Region: Choose closest to your users
- [ ] Plan: Free or Paid
- [ ] Click "Create Database"
- [ ] Note down connection details

#### Step 2: Create Web Service
- [ ] Click "New" → "Web Service"
- [ ] Connect repository
- [ ] Select your repository
- [ ] Configure service:
  - [ ] Name: `eventzone-backend`
  - [ ] Region: Same as database
  - [ ] Branch: `main`
  - [ ] Runtime: Docker
  - [ ] Plan: Free or Paid

#### Step 3: Set Environment Variables
Copy these from your database and set in web service:
- [ ] `SPRING_PROFILES_ACTIVE=prod`
- [ ] `DATABASE_URL=<internal connection string from database>`
- [ ] `DB_USERNAME=<from database>`
- [ ] `DB_PASSWORD=<from database>`
- [ ] `PORT=8080`
- [ ] `JWT_SECRET=<generate secure random string>`
- [ ] `JWT_EXPIRATION=86400000`
- [ ] `ALLOWED_ORIGINS=<your frontend URL>`

#### Step 4: Configure Advanced Settings
- [ ] Health Check Path: `/actuator/health`
- [ ] Auto-Deploy: Enabled
- [ ] Click "Create Web Service"

## ✨ Post-Deployment Verification

### 1. Check Service Status
- [ ] Web service is "Live" (green indicator)
- [ ] Database is "Available"
- [ ] No error messages in logs

### 2. Test Endpoints
- [ ] Health check: `https://your-app.onrender.com/actuator/health`
  - Expected: `{"status":"UP"}`
- [ ] API docs: `https://your-app.onrender.com/swagger-ui.html`
  - Expected: Swagger UI loads successfully
- [ ] API specification: `https://your-app.onrender.com/api-docs`
  - Expected: JSON API specification

### 3. Test API Functionality
- [ ] Register a new user
- [ ] Login with credentials
- [ ] Test authenticated endpoints
- [ ] Verify database persistence

### 4. Review Logs
- [ ] Check for any error messages
- [ ] Verify application startup logs
- [ ] Confirm database connection established

## 🔧 Configuration Updates

### Update Frontend URL
After frontend deployment:
1. [ ] Go to web service settings
2. [ ] Update `ALLOWED_ORIGINS` environment variable
3. [ ] Add frontend URL (e.g., `https://eventzone-frontend.onrender.com`)
4. [ ] Service will automatically redeploy

### Enable CORS in Code
If not already done, update `SecurityConfig.java`:
```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    String allowedOrigins = System.getenv()
        .getOrDefault("ALLOWED_ORIGINS", "*");
    configuration.setAllowedOrigins(Arrays.asList(allowedOrigins.split(",")));
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(Arrays.asList("*"));
    configuration.setAllowCredentials(true);
    
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
}
```

## 📊 Monitoring

### Daily Checks (First Week)
- [ ] Monitor service health
- [ ] Review error logs
- [ ] Check response times
- [ ] Verify database connections

### Weekly Checks
- [ ] Review metrics
- [ ] Check database usage
- [ ] Monitor bandwidth usage
- [ ] Review security logs

## 🐛 Troubleshooting

### Common Issues

#### Service Won't Start
- [ ] Check logs for error messages
- [ ] Verify all environment variables are set
- [ ] Ensure DATABASE_URL is correct
- [ ] Check Java version (should be 21)

#### Database Connection Failed
- [ ] Verify DATABASE_URL format
- [ ] Check database is in same region
- [ ] Ensure database is "Available"
- [ ] Verify credentials

#### Slow Performance
- [ ] Free tier spins down after 15 minutes inactivity
- [ ] Consider upgrading to paid tier
- [ ] Optimize database queries
- [ ] Enable connection pooling (already configured)

#### CORS Errors
- [ ] Update ALLOWED_ORIGINS with correct frontend URL
- [ ] Verify CORS configuration in SecurityConfig
- [ ] Check browser console for specific error

## 💰 Cost Management

### Free Tier Limits
- **Web Service**: 
  - 750 hours/month
  - Spins down after 15 minutes inactivity
  - Restarts on incoming request (30-60 seconds)
  
- **Database**:
  - 90 days free (then $7/month)
  - 1GB storage
  - Auto backups (7 days retention)

### Upgrade Considerations
Consider paid tier if you need:
- [ ] Always-on service (no spin down)
- [ ] Faster response times
- [ ] More resources (RAM/CPU)
- [ ] Custom domains
- [ ] Better SLA

## 📝 Documentation

### Save These URLs
- [ ] Application URL: `https://your-app-name.onrender.com`
- [ ] Swagger UI: `https://your-app-name.onrender.com/swagger-ui.html`
- [ ] Health Check: `https://your-app-name.onrender.com/actuator/health`
- [ ] Database Connection: (from Render dashboard)

### Update Documentation
- [ ] Update README.md with deployment URL
- [ ] Document environment variables
- [ ] Add API endpoint examples
- [ ] Include troubleshooting section

## 🎉 Success Criteria

Your deployment is successful when:
- [ ] All health checks pass
- [ ] Application is accessible via public URL
- [ ] API endpoints respond correctly
- [ ] Database operations work
- [ ] Authentication/Authorization works
- [ ] CORS is configured for frontend
- [ ] Logs show no critical errors

## 🔄 Continuous Deployment

### Automatic Deployments
Once configured, Render will automatically:
- [ ] Detect new commits to configured branch
- [ ] Build new Docker image
- [ ] Run health checks
- [ ] Deploy if successful
- [ ] Rollback if health checks fail

### Manual Deployments
To deploy manually:
1. [ ] Go to service dashboard
2. [ ] Click "Manual Deploy"
3. [ ] Select branch
4. [ ] Click "Deploy"

## 📞 Support Resources

- [ ] Render Documentation: https://render.com/docs
- [ ] Community Forum: https://community.render.com
- [ ] Status Page: https://status.render.com
- [ ] Support Email: support@render.com

---

**Last Updated**: 2026-07-01
**Deployment Target**: Render.com
**Application**: EventZone Backend
