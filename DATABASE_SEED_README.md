# EventZone Database Seed Data

## 📋 Overview
This document describes the sample data created in the `database_seed.sql` script for testing and development.

## 🔐 Default Credentials
**All users have the same password:** `Password@1`

## 👥 User Accounts

### Admin Users (Role: ADMIN)
| Name | Email | Role |
|------|-------|------|
| Admin John | admin.john@eventzone.com | ADMIN |
| Admin Sarah | admin.sarah@eventzone.com | ADMIN |
| Admin Mike | admin.mike@eventzone.com | ADMIN |

### Organiser Users (Role: ORGANISER)
| Name | Email | Role |
|------|-------|------|
| David Wilson | david.wilson@events.com | ORGANISER |
| Emma Thompson | emma.thompson@events.com | ORGANISER |
| Robert Chen | robert.chen@events.com | ORGANISER |
| Priya Sharma | priya.sharma@events.com | ORGANISER |
| James Anderson | james.anderson@events.com | ORGANISER |

### Attendee Users (Role: ATTENDEE)
| Name | Email | Role |
|------|-------|------|
| Alice Johnson | alice.johnson@gmail.com | ATTENDEE |
| Bob Martinez | bob.martinez@gmail.com | ATTENDEE |
| Carol Davis | carol.davis@gmail.com | ATTENDEE |
| Daniel Kumar | daniel.kumar@gmail.com | ATTENDEE |
| Emily Brown | emily.brown@gmail.com | ATTENDEE |
| Frank Liu | frank.liu@gmail.com | ATTENDEE |
| Grace Lee | grace.lee@gmail.com | ATTENDEE |

## 🎫 Event Categories (8 Categories)
1. Concert
2. Conference
3. Sports
4. Workshop
5. Theater
6. Festival
7. Exhibition
8. Comedy Show

## 🎉 Sample Events (12 Events)

### 1. Rock Legends Live 2026
- **Category:** Concert
- **Date:** Aug 15, 2026 - 7:00 PM
- **Venue:** Chennai Trade Centre, Chennai
- **Organiser:** David Wilson
- **Ticket Categories:**
  - VIP: ₹5,999 (100 seats)
  - Premium: ₹3,999 (200 seats)
  - General: ₹1,999 (500 seats)
  - Early Bird: ₹1,499 (200 seats, 20 already booked)

### 2. Classical Music Evening
- **Category:** Concert
- **Date:** Sep 20, 2026 - 6:30 PM
- **Venue:** Music Academy, Chennai
- **Organiser:** Emma Thompson
- **Ticket Categories:**
  - VIP: ₹2,999 (50 seats)
  - Standard: ₹1,499 (150 seats)
  - Balcony: ₹999 (100 seats)

### 3. Tech Summit 2026
- **Category:** Conference
- **Date:** Oct 5, 2026 - 9:00 AM
- **Venue:** HITEX Exhibition Centre, Hyderabad
- **Organiser:** Robert Chen
- **Ticket Categories:**
  - Full Conference Pass: ₹8,999 (500 seats, 50 booked)
  - Single Day Pass: ₹3,499 (300 seats, 20 booked)
  - Student Pass: ₹1,999 (200 seats)
  - Virtual Access: ₹999 (1000 seats)

### 4. Healthcare Innovation Conference
- **Category:** Conference
- **Date:** Nov 12, 2026 - 8:30 AM
- **Venue:** ITC Grand Chola, Chennai
- **Organiser:** Priya Sharma
- **Ticket Categories:**
  - Professional: ₹12,999 (300 seats)
  - Academic: ₹6,999 (150 seats)
  - Virtual Only: ₹2,999 (500 seats)

### 5. Chennai Cricket Championship Finals
- **Category:** Sports
- **Date:** Dec 1, 2026 - 2:00 PM
- **Venue:** M.A. Chidambaram Stadium, Chennai
- **Organiser:** James Anderson
- **Ticket Categories:**
  - Pavilion: ₹3,999 (200 seats, 50 booked)
  - East Stand: ₹1,999 (500 seats, 100 booked)
  - West Stand: ₹1,999 (500 seats, 20 booked)
  - General: ₹799 (1000 seats)

### 6. Marathon Chennai 2026
- **Category:** Sports
- **Date:** Aug 25, 2026 - 5:00 AM
- **Venue:** Marina Beach, Chennai
- **Organiser:** David Wilson
- **Ticket Categories:**
  - Full Marathon: ₹1,999 (500 seats)
  - 10K Run: ₹799 (1000 seats, 80 booked)
  - 5K Fun Run: ₹499 (2000 seats)

### 7. Digital Marketing Masterclass
- **Category:** Workshop
- **Date:** Sep 8, 2026 - 10:00 AM
- **Venue:** WeWork, Bangalore
- **Organiser:** Emma Thompson
- **Ticket Categories:**
  - Premium (with Materials): ₹4,999 (50 seats, 5 booked)
  - Standard: ₹2,999 (100 seats)

### 8. Photography Workshop
- **Category:** Workshop
- **Date:** Oct 18, 2026 - 9:00 AM
- **Venue:** Creative Hub, Chennai
- **Organiser:** Robert Chen
- **Ticket Categories:**
  - Premium (with Equipment): ₹5,999 (20 seats)
  - Standard (BYO Equipment): ₹3,499 (30 seats, 2 booked)

### 9. Shakespeare's Hamlet
- **Category:** Theater
- **Date:** Nov 20, 2026 - 7:30 PM
- **Venue:** The Music Academy, Chennai
- **Organiser:** Priya Sharma
- **Ticket Categories:**
  - Stalls: ₹1,999 (80 seats)
  - Circle: ₹1,299 (100 seats)
  - Balcony: ₹799 (120 seats)

### 10. Chennai Food Festival 2026
- **Category:** Festival
- **Date:** Dec 15, 2026 - 11:00 AM
- **Venue:** Island Grounds, Chennai
- **Organiser:** James Anderson
- **Ticket Categories:**
  - VIP Food Pass: ₹2,999 (200 seats)
  - General Entry: ₹499 (5000 seats, 200 booked)
  - Family Pass (4 people): ₹1,499 (1000 seats)

### 11. Art & Design Expo 2026
- **Category:** Exhibition
- **Date:** Sep 30, 2026 - 10:00 AM
- **Venue:** Lalit Kala Akademi, Chennai
- **Organiser:** David Wilson
- **Ticket Categories:**
  - Collector Pass: ₹1,999 (50 seats)
  - Standard Entry: ₹499 (500 seats)
  - Student Entry: ₹199 (300 seats)

### 12. Stand-Up Comedy Night
- **Category:** Comedy Show
- **Date:** Oct 28, 2026 - 8:00 PM
- **Venue:** Phoenix MarketCity, Chennai
- **Organiser:** Emma Thompson
- **Ticket Categories:**
  - Front Row: ₹2,499 (20 seats, 5 booked)
  - Premium: ₹1,499 (100 seats, 15 booked)
  - Standard: ₹999 (200 seats)

## 📊 Sample Bookings (12 Bookings)
- Alice Johnson: 2 bookings (Rock concert, Marathon)
- Bob Martinez: 2 bookings (Cricket match, Comedy show - cancelled)
- Carol Davis: 2 bookings (Tech conference, Workshop)
- Daniel Kumar: 1 booking (Food festival family pass)
- Emily Brown: 2 bookings (Photography workshop, Comedy show)
- Frank Liu: 1 booking (Cricket match)
- Grace Lee: 1 booking (Tech conference)

## 🚀 How to Use

### For Render PostgreSQL:
1. Go to your Render Dashboard
2. Select your `eventzone-db` database
3. Click on "Connect" → "External Connection"
4. Use the provided PSQL command or any PostgreSQL client
5. Copy and paste the entire content of `database_seed.sql`
6. Execute the script

### Using psql command line:
```bash
psql -h <your-render-host> -U eventzone_user -d eventzone_jec1 -f database_seed.sql
```

### Using pgAdmin or any GUI tool:
1. Connect to your Render PostgreSQL database
2. Open Query Tool
3. Load the `database_seed.sql` file
4. Execute

## ⚠️ Important Notes
- This script **drops all existing tables** and recreates them
- All existing data will be lost
- Use this only for development/testing environments
- The BCrypt password hash is pre-generated for `Password@1`

## 🔍 Verification Queries
After running the script, verify the data:

```sql
-- Count records in each table
SELECT 'Event Categories' as table_name, COUNT(*) as record_count FROM event_categories
UNION ALL
SELECT 'Users', COUNT(*) FROM users
UNION ALL
SELECT 'Events', COUNT(*) FROM events
UNION ALL
SELECT 'Ticket Categories', COUNT(*) FROM ticket_categories
UNION ALL
SELECT 'Bookings', COUNT(*) FROM bookings;
```

Expected results:
- Event Categories: 8
- Users: 15
- Events: 12
- Ticket Categories: 40+
- Bookings: 12

## 📝 Testing API with Sample Data

### Login as Admin:
```json
POST /api/auth/login
{
  "email": "admin.john@eventzone.com",
  "password": "Password@1"
}
```

### Login as Organiser:
```json
POST /api/auth/login
{
  "email": "david.wilson@events.com",
  "password": "Password@1"
}
```

### Login as Attendee:
```json
POST /api/auth/login
{
  "email": "alice.johnson@gmail.com",
  "password": "Password@1"
}
```

## 📞 Support
If you encounter any issues:
1. Check PostgreSQL logs in Render
2. Verify database connection settings
3. Ensure the database user has proper permissions
4. Check for any constraint violations in the logs
