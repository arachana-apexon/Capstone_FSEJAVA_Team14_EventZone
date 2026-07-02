# Quick Start Guide - Render Deployment

## 🚀 5-Minute Deployment

### Step 1: Push to GitHub (2 minutes)
```bash
git add .
git commit -m "Add Render deployment configuration"
git push origin main
```

### Step 2: Deploy on Render (3 minutes)
1. Go to [render.com/dashboard](https://dashboard.render.com/)
2. Click **"New"** → **"Blueprint"**
3. Connect your repository
4. Click **"Apply"**
5. Wait for deployment (5-10 minutes)

### Step 3: Update Environment Variables
After deployment:
1. Go to your web service
2. Click **"Environment"**
3. Update **`ALLOWED_ORIGINS`** with your frontend URL
4. Save changes

**Done!** 🎉 Your API is live at: `https://eventzone-backend.onrender.com`

---

## 📋 Environment Variables Reference

### Required Variables (Auto-configured by Blueprint)
```
SPRING_PROFILES_ACTIVE=prod
DATABASE_URL=<auto-from-database>
DB_USERNAME=<auto-from-database>
DB_PASSWORD=<auto-from-database>
PORT=8080
```

### Custom Variables (You need to set)
```
JWT_SECRET=<generate-a-secure-random-string>
JWT_EXPIRATION=86400000
ALLOWED_ORIGINS=https://your-frontend-app.com
```

### Optional Variables
```
LOGGING_LEVEL=INFO
```

---

## 🔗 Important URLs

After deployment, save these URLs:

| Resource | URL Pattern | Example |
|----------|-------------|---------|
| **API Base** | `https://your-app.onrender.com` | Test with Postman |
| **Health Check** | `/actuator/health` | Should return `{"status":"UP"}` |
| **API Docs** | `/swagger-ui.html` | Interactive API documentation |
| **OpenAPI Spec** | `/api-docs` | JSON API specification |

---

## 🧪 Testing Your Deployment

### 1. Health Check
```bash
curl https://your-app.onrender.com/actuator/health
```
Expected: `{"status":"UP"}`

### 2. API Documentation
Open in browser:
```
https://your-app.onrender.com/swagger-ui.html
```

### 3. Test Authentication
```bash
# Register
curl -X POST https://your-app.onrender.com/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"test","email":"test@example.com","password":"password123"}'

# Login
curl -X POST https://your-app.onrender.com/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123"}'
```

---

## ⚠️ Important Notes

### Free Tier Limitations
- **Spin Down**: Service sleeps after 15 minutes of inactivity
- **Wake Up**: First request takes 30-60 seconds
- **Database**: Free for 90 days, then $7/month

### Security Checklist
✅ Never commit `.env` files  
✅ Use environment variables for secrets  
✅ Generate strong JWT secret  
✅ Update CORS origins for production  
✅ Use HTTPS in production  

### Performance Tips
- Enable HTTP compression ✓ (already configured)
- Use connection pooling ✓ (already configured)
- Monitor with Actuator ✓ (already configured)
- Consider upgrading to paid tier for always-on service

---

## 🆘 Quick Troubleshooting

| Problem | Solution |
|---------|----------|
| Service won't start | Check logs → Verify environment variables |
| Database connection error | Verify DATABASE_URL format and credentials |
| CORS errors | Update `ALLOWED_ORIGINS` with your frontend URL |
| 502 Bad Gateway | Service is starting (wait 30-60 seconds) |
| Slow first request | Free tier spin down (normal behavior) |

---

## 📞 Need Help?

1. **Check Logs**: Service Dashboard → Logs tab
2. **Review Metrics**: Service Dashboard → Metrics tab  
3. **Read Docs**: See [DEPLOYMENT.md](DEPLOYMENT.md) for detailed guide
4. **Check Status**: [status.render.com](https://status.render.com)
5. **Community**: [community.render.com](https://community.render.com)

---

## 🎯 Next Steps

After successful deployment:

1. ✅ Test all API endpoints
2. ✅ Deploy your frontend
3. ✅ Update CORS settings
4. ✅ Set up custom domain (optional)
5. ✅ Monitor application logs
6. ✅ Set up error alerting (optional)

---

**Your API is ready!** Share the URL with your team and start building! 🚀
