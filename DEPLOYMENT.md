# Deployment Guide for Render

This guide will help you deploy the EventZone Backend application to Render.

## Prerequisites

1. A [Render account](https://render.com/) (sign up for free)
2. Your code pushed to a GitHub/GitLab repository
3. PostgreSQL database credentials (Render provides a free tier)

## Deployment Options

### Option 1: Using Render Blueprint (Recommended)

This method uses the `render.yaml` file to automatically create both the web service and database.

1. **Push your code to GitHub/GitLab**
   ```bash
   git add .
   git commit -m "Add Render deployment configuration"
   git push origin main
   ```

2. **Create New Blueprint Instance**
   - Go to [Render Dashboard](https://dashboard.render.com/)
   - Click "New" → "Blueprint"
   - Connect your repository
   - Render will automatically detect `render.yaml`
   - Review the services to be created:
     - Web Service: `eventzone-backend`
     - PostgreSQL Database: `eventzone-db`
   - Click "Apply" to create the services

3. **Update Environment Variables**
   - After deployment, go to your web service settings
   - Update `ALLOWED_ORIGINS` with your frontend URL
   - If you have a JWT secret, update `JWT_SECRET`

### Option 2: Manual Setup

#### Step 1: Create PostgreSQL Database

1. Go to Render Dashboard
2. Click "New" → "PostgreSQL"
3. Configure:
   - **Name**: `eventzone-db`
   - **Database**: `eventzone`
   - **User**: `eventzone_user`
   - **Region**: Choose closest to your users
   - **Plan**: Free (or paid for better performance)
4. Click "Create Database"
5. Wait for provisioning and note the connection details

#### Step 2: Create Web Service

1. Click "New" → "Web Service"
2. Connect your repository
3. Configure:
   - **Name**: `eventzone-backend`
   - **Region**: Same as your database
   - **Branch**: `main` (or your default branch)
   - **Runtime**: `Docker`
   - **Plan**: Free (or paid for better performance)

4. **Set Environment Variables**:
   ```
   SPRING_PROFILES_ACTIVE=prod
   DATABASE_URL=<from database internal connection string>
   DB_USERNAME=<from database user>
   DB_PASSWORD=<from database password>
   PORT=8080
   JWT_SECRET=<generate a secure random string>
   JWT_EXPIRATION=86400000
   ALLOWED_ORIGINS=https://your-frontend-app.onrender.com
   ```

5. **Advanced Settings**:
   - **Health Check Path**: `/actuator/health`
   - **Auto-Deploy**: Yes

6. Click "Create Web Service"

## Post-Deployment

### 1. Verify Deployment

Once deployed, your application will be available at:
```
https://eventzone-backend.onrender.com
```

### 2. Test API Endpoints

- **Health Check**: `https://eventzone-backend.onrender.com/actuator/health`
- **API Docs**: `https://eventzone-backend.onrender.com/swagger-ui.html`
- **API Specification**: `https://eventzone-backend.onrender.com/api-docs`

### 3. Add Spring Boot Actuator (Optional but Recommended)

Add to `pom.xml` for better health checks:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

Add to `application-prod.properties`:
```properties
management.endpoints.web.exposure.include=health,info
management.endpoint.health.show-details=when-authorized
```

### 4. Configure CORS

Update your `SecurityConfig.java` or create a CORS configuration to allow your frontend:

```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(Arrays.asList(
        System.getenv().getOrDefault("ALLOWED_ORIGINS", "*").split(",")
    ));
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(Arrays.asList("*"));
    configuration.setAllowCredentials(true);
    
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
}
```

## Monitoring and Logs

1. **View Logs**: Go to your service → "Logs" tab
2. **Monitor Performance**: Check the "Metrics" tab
3. **Health Checks**: Render automatically monitors your health check endpoint

## Database Management

### Connecting to PostgreSQL

Use the connection details from Render:

```bash
psql -h <hostname> -U <username> -d <database>
```

### Backup Database

Render Free tier includes:
- Automatic daily backups (retained for 7 days)
- Manual backups via dashboard

## Troubleshooting

### Application Won't Start

1. Check logs for errors
2. Verify environment variables are set correctly
3. Ensure database is running and accessible
4. Check DATABASE_URL format

### Database Connection Issues

1. Verify DATABASE_URL includes all parameters
2. Check if database is in the same region
3. Ensure connection pool settings are appropriate

### Slow Performance on Free Tier

- Free tier services spin down after 15 minutes of inactivity
- First request after spin-down takes 30-60 seconds
- Consider upgrading to paid tier for always-on service

## Upgrading from Free Tier

To improve performance:

1. **Web Service**: Upgrade to Starter ($7/month) or higher
   - Always-on (no spin down)
   - More resources (RAM, CPU)
   - Better performance

2. **Database**: Upgrade to Starter ($7/month) or higher
   - More storage
   - Better performance
   - Point-in-time recovery

## Additional Resources

- [Render Documentation](https://render.com/docs)
- [Deploying Spring Boot on Render](https://render.com/docs/deploy-spring-boot)
- [Render Free Tier](https://render.com/docs/free)

## Support

- Render Community: [community.render.com](https://community.render.com/)
- Documentation: [render.com/docs](https://render.com/docs)
