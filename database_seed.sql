-- =====================================================
-- EventZone Database Seed Script
-- This script creates tables and populates with sample data
-- Default password for all users: Password@1
-- =====================================================

-- Drop tables if they exist (for clean setup)
DROP TABLE IF EXISTS bookings CASCADE;
DROP TABLE IF EXISTS ticket_categories CASCADE;
DROP TABLE IF EXISTS events CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS event_categories CASCADE;

-- =====================================================
-- CREATE TABLES
-- =====================================================

-- Table: event_categories
CREATE TABLE event_categories (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Table: users
CREATE TABLE users (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL DEFAULT 'ATTENDEE',
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_role CHECK (role IN ('ADMIN', 'ORGANISER', 'ATTENDEE'))
);

-- Table: events
CREATE TABLE events (
    id UUID PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    event_date TIMESTAMP NOT NULL,
    venue VARCHAR(255) NOT NULL,
    cover_image_url VARCHAR(500),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    category_id UUID NOT NULL,
    organiser_id UUID NOT NULL,
    CONSTRAINT fk_events_category FOREIGN KEY (category_id) REFERENCES event_categories(id),
    CONSTRAINT fk_events_organiser FOREIGN KEY (organiser_id) REFERENCES users(id)
);

-- Table: ticket_categories
CREATE TABLE ticket_categories (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    total_seats INTEGER NOT NULL,
    available_seats INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    event_id UUID NOT NULL,
    CONSTRAINT fk_ticket_categories_event FOREIGN KEY (event_id) REFERENCES events(id),
    CONSTRAINT chk_available_seats CHECK (available_seats >= 0 AND available_seats <= total_seats)
);

-- Table: bookings
CREATE TABLE bookings (
    id UUID PRIMARY KEY,
    booking_ref VARCHAR(50) NOT NULL UNIQUE,
    quantity INTEGER NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'CONFIRMED',
    booked_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_id UUID NOT NULL,
    ticket_category_id UUID NOT NULL,
    CONSTRAINT fk_bookings_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_bookings_ticket_category FOREIGN KEY (ticket_category_id) REFERENCES ticket_categories(id),
    CONSTRAINT chk_quantity CHECK (quantity > 0),
    CONSTRAINT chk_status CHECK (status IN ('CONFIRMED', 'CANCELLED', 'PENDING'))
);

-- =====================================================
-- INSERT SAMPLE DATA
-- =====================================================

-- =====================================================
-- 1. EVENT CATEGORIES
-- =====================================================
INSERT INTO event_categories (id, name, active, created_at) VALUES
('a1b2c3d4-1111-1111-1111-111111111111', 'Concert', TRUE, CURRENT_TIMESTAMP),
('a1b2c3d4-2222-2222-2222-222222222222', 'Conference', TRUE, CURRENT_TIMESTAMP),
('a1b2c3d4-3333-3333-3333-333333333333', 'Sports', TRUE, CURRENT_TIMESTAMP),
('a1b2c3d4-4444-4444-4444-444444444444', 'Workshop', TRUE, CURRENT_TIMESTAMP),
('a1b2c3d4-5555-5555-5555-555555555555', 'Theater', TRUE, CURRENT_TIMESTAMP),
('a1b2c3d4-6666-6666-6666-666666666666', 'Festival', TRUE, CURRENT_TIMESTAMP),
('a1b2c3d4-7777-7777-7777-777777777777', 'Exhibition', TRUE, CURRENT_TIMESTAMP),
('a1b2c3d4-8888-8888-8888-888888888888', 'Comedy Show', TRUE, CURRENT_TIMESTAMP);

-- =====================================================
-- 2. USERS (Password: Password@1)
-- BCrypt hash: $2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi
-- =====================================================

-- ADMIN Users
INSERT INTO users (id, name, email, password, role, active, created_at) VALUES
('b1b1b1b1-1111-1111-1111-111111111111', 'Admin John', 'admin.john@eventzone.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ADMIN', TRUE, CURRENT_TIMESTAMP),
('b1b1b1b1-2222-2222-2222-222222222222', 'Admin Sarah', 'admin.sarah@eventzone.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ADMIN', TRUE, CURRENT_TIMESTAMP),
('b1b1b1b1-3333-3333-3333-333333333333', 'Admin Mike', 'admin.mike@eventzone.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ADMIN', TRUE, CURRENT_TIMESTAMP);

-- ORGANISER Users
INSERT INTO users (id, name, email, password, role, active, created_at) VALUES
('c1c1c1c1-1111-1111-1111-111111111111', 'David Wilson', 'david.wilson@events.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ORGANISER', TRUE, CURRENT_TIMESTAMP),
('c1c1c1c1-2222-2222-2222-222222222222', 'Emma Thompson', 'emma.thompson@events.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ORGANISER', TRUE, CURRENT_TIMESTAMP),
('c1c1c1c1-3333-3333-3333-333333333333', 'Robert Chen', 'robert.chen@events.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ORGANISER', TRUE, CURRENT_TIMESTAMP),
('c1c1c1c1-4444-4444-4444-444444444444', 'Priya Sharma', 'priya.sharma@events.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ORGANISER', TRUE, CURRENT_TIMESTAMP),
('c1c1c1c1-5555-5555-5555-555555555555', 'James Anderson', 'james.anderson@events.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ORGANISER', TRUE, CURRENT_TIMESTAMP);

-- ATTENDEE Users
INSERT INTO users (id, name, email, password, role, active, created_at) VALUES
('d1d1d1d1-1111-1111-1111-111111111111', 'Alice Johnson', 'alice.johnson@gmail.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ATTENDEE', TRUE, CURRENT_TIMESTAMP),
('d1d1d1d1-2222-2222-2222-222222222222', 'Bob Martinez', 'bob.martinez@gmail.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ATTENDEE', TRUE, CURRENT_TIMESTAMP),
('d1d1d1d1-3333-3333-3333-333333333333', 'Carol Davis', 'carol.davis@gmail.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ATTENDEE', TRUE, CURRENT_TIMESTAMP),
('d1d1d1d1-4444-4444-4444-444444444444', 'Daniel Kumar', 'daniel.kumar@gmail.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ATTENDEE', TRUE, CURRENT_TIMESTAMP),
('d1d1d1d1-5555-5555-5555-555555555555', 'Emily Brown', 'emily.brown@gmail.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ATTENDEE', TRUE, CURRENT_TIMESTAMP),
('d1d1d1d1-6666-6666-6666-666666666666', 'Frank Liu', 'frank.liu@gmail.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ATTENDEE', TRUE, CURRENT_TIMESTAMP),
('d1d1d1d1-7777-7777-7777-777777777777', 'Grace Lee', 'grace.lee@gmail.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ATTENDEE', TRUE, CURRENT_TIMESTAMP);

-- =====================================================
-- 3. EVENTS (Multiple events with different categories)
-- =====================================================

-- Concert Events
INSERT INTO events (id, title, description, event_date, venue, cover_image_url, active, created_at, category_id, organiser_id) VALUES
('e1e1e1e1-1111-1111-1111-111111111111', 
'Rock Legends Live 2026', 
'Experience the greatest rock bands of all time in one unforgettable night! Featuring tribute acts and special guests.',
'2026-08-15 19:00:00', 
'Chennai Trade Centre, Chennai',
'https://images.unsplash.com/photo-1501281668745-f7f57925c3b4?w=800',
TRUE, CURRENT_TIMESTAMP,
'a1b2c3d4-1111-1111-1111-111111111111',
'c1c1c1c1-1111-1111-1111-111111111111'),

('e1e1e1e1-2222-2222-2222-222222222222',
'Classical Music Evening',
'A mesmerizing evening of classical compositions performed by the Chennai Symphony Orchestra.',
'2026-09-20 18:30:00',
'Music Academy, Chennai',
'https://images.unsplash.com/photo-1465847899084-d164df4dedc6?w=800',
TRUE, CURRENT_TIMESTAMP,
'a1b2c3d4-1111-1111-1111-111111111111',
'c1c1c1c1-2222-2222-2222-222222222222'),

-- Conference Events
('e1e1e1e1-3333-3333-3333-333333333333',
'Tech Summit 2026',
'Join industry leaders and tech enthusiasts for insights on AI, Cloud Computing, and Future Technologies.',
'2026-10-05 09:00:00',
'HITEX Exhibition Centre, Hyderabad',
'https://images.unsplash.com/photo-1540575467063-178a50c2df87?w=800',
TRUE, CURRENT_TIMESTAMP,
'a1b2c3d4-2222-2222-2222-222222222222',
'c1c1c1c1-3333-3333-3333-333333333333'),

('e1e1e1e1-4444-4444-4444-444444444444',
'Healthcare Innovation Conference',
'Discover breakthrough innovations in healthcare technology and patient care solutions.',
'2026-11-12 08:30:00',
'ITC Grand Chola, Chennai',
'https://images.unsplash.com/photo-1591115765373-5207764f72e7?w=800',
TRUE, CURRENT_TIMESTAMP,
'a1b2c3d4-2222-2222-2222-222222222222',
'c1c1c1c1-4444-4444-4444-444444444444'),

-- Sports Events
('e1e1e1e1-5555-5555-5555-555555555555',
'Chennai Cricket Championship Finals',
'Witness the thrilling finals of the Chennai Premier League. Two top teams battle for the championship!',
'2026-12-01 14:00:00',
'M.A. Chidambaram Stadium, Chennai',
'https://images.unsplash.com/photo-1531415074968-036ba1b575da?w=800',
TRUE, CURRENT_TIMESTAMP,
'a1b2c3d4-3333-3333-3333-333333333333',
'c1c1c1c1-5555-5555-5555-555555555555'),

('e1e1e1e1-6666-6666-6666-666666666666',
'Marathon Chennai 2026',
'Annual marathon event promoting health and fitness. 5K, 10K, and Full Marathon categories available.',
'2026-08-25 05:00:00',
'Marina Beach, Chennai',
'https://images.unsplash.com/photo-1452626038306-9aae5e071dd3?w=800',
TRUE, CURRENT_TIMESTAMP,
'a1b2c3d4-3333-3333-3333-333333333333',
'c1c1c1c1-1111-1111-1111-111111111111'),

-- Workshop Events
('e1e1e1e1-7777-7777-7777-777777777777',
'Digital Marketing Masterclass',
'Learn advanced strategies for SEO, Social Media Marketing, and Content Marketing from industry experts.',
'2026-09-08 10:00:00',
'WeWork, Bangalore',
'https://images.unsplash.com/photo-1557804506-669a67965ba0?w=800',
TRUE, CURRENT_TIMESTAMP,
'a1b2c3d4-4444-4444-4444-444444444444',
'c1c1c1c1-2222-2222-2222-222222222222'),

('e1e1e1e1-8888-8888-8888-888888888888',
'Photography Workshop: Mastering Portrait Photography',
'Hands-on workshop covering lighting, composition, and post-processing techniques.',
'2026-10-18 09:00:00',
'Creative Hub, Chennai',
'https://images.unsplash.com/photo-1542038784456-1ea8e935640e?w=800',
TRUE, CURRENT_TIMESTAMP,
'a1b2c3d4-4444-4444-4444-444444444444',
'c1c1c1c1-3333-3333-3333-333333333333'),

-- Theater Events
('e1e1e1e1-9999-9999-9999-999999999999',
'Shakespeare''s Hamlet',
'A captivating adaptation of the classic tragedy performed by Chennai Theater Company.',
'2026-11-20 19:30:00',
'The Music Academy, Chennai',
'https://images.unsplash.com/photo-1503095396549-807759245b35?w=800',
TRUE, CURRENT_TIMESTAMP,
'a1b2c3d4-5555-5555-5555-555555555555',
'c1c1c1c1-4444-4444-4444-444444444444'),

-- Festival Events
('e1e1e1e1-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
'Chennai Food Festival 2026',
'Celebrate culinary diversity with over 100 food stalls featuring cuisines from around the world.',
'2026-12-15 11:00:00',
'Island Grounds, Chennai',
'https://images.unsplash.com/photo-1555939594-58d7cb561ad1?w=800',
TRUE, CURRENT_TIMESTAMP,
'a1b2c3d4-6666-6666-6666-666666666666',
'c1c1c1c1-5555-5555-5555-555555555555'),

-- Exhibition Events
('e1e1e1e1-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
'Art & Design Expo 2026',
'Contemporary art exhibition featuring works from emerging and established artists.',
'2026-09-30 10:00:00',
'Lalit Kala Akademi, Chennai',
'https://images.unsplash.com/photo-1561214115-f2f134cc4912?w=800',
TRUE, CURRENT_TIMESTAMP,
'a1b2c3d4-7777-7777-7777-777777777777',
'c1c1c1c1-1111-1111-1111-111111111111'),

-- Comedy Show Events
('e1e1e1e1-cccc-cccc-cccc-cccccccccccc',
'Stand-Up Comedy Night',
'An evening of laughter with top comedians from across India. Adults only.',
'2026-10-28 20:00:00',
'Phoenix MarketCity, Chennai',
'https://images.unsplash.com/photo-1527224857830-43a7acc85260?w=800',
TRUE, CURRENT_TIMESTAMP,
'a1b2c3d4-8888-8888-8888-888888888888',
'c1c1c1c1-2222-2222-2222-222222222222');

-- =====================================================
-- 4. TICKET CATEGORIES (Multiple categories per event)
-- =====================================================

-- Rock Legends Live 2026
INSERT INTO ticket_categories (id, name, price, total_seats, available_seats, created_at, event_id) VALUES
('f1f1f1f1-1111-1111-1111-111111111111', 'VIP', 5999.00, 100, 100, CURRENT_TIMESTAMP, 'e1e1e1e1-1111-1111-1111-111111111111'),
('f1f1f1f1-1112-1111-1111-111111111111', 'Premium', 3999.00, 200, 200, CURRENT_TIMESTAMP, 'e1e1e1e1-1111-1111-1111-111111111111'),
('f1f1f1f1-1113-1111-1111-111111111111', 'General', 1999.00, 500, 500, CURRENT_TIMESTAMP, 'e1e1e1e1-1111-1111-1111-111111111111'),
('f1f1f1f1-1114-1111-1111-111111111111', 'Early Bird', 1499.00, 200, 180, CURRENT_TIMESTAMP, 'e1e1e1e1-1111-1111-1111-111111111111');

-- Classical Music Evening
INSERT INTO ticket_categories (id, name, price, total_seats, available_seats, created_at, event_id) VALUES
('f2f2f2f2-2221-1111-1111-111111111111', 'VIP', 2999.00, 50, 50, CURRENT_TIMESTAMP, 'e1e1e1e1-2222-2222-2222-222222222222'),
('f2f2f2f2-2222-1111-1111-111111111111', 'Standard', 1499.00, 150, 150, CURRENT_TIMESTAMP, 'e1e1e1e1-2222-2222-2222-222222222222'),
('f2f2f2f2-2223-1111-1111-111111111111', 'Balcony', 999.00, 100, 100, CURRENT_TIMESTAMP, 'e1e1e1e1-2222-2222-2222-222222222222');

-- Tech Summit 2026
INSERT INTO ticket_categories (id, name, price, total_seats, available_seats, created_at, event_id) VALUES
('f3f3f3f3-3331-1111-1111-111111111111', 'Full Conference Pass', 8999.00, 500, 450, CURRENT_TIMESTAMP, 'e1e1e1e1-3333-3333-3333-333333333333'),
('f3f3f3f3-3332-1111-1111-111111111111', 'Single Day Pass', 3499.00, 300, 280, CURRENT_TIMESTAMP, 'e1e1e1e1-3333-3333-3333-333333333333'),
('f3f3f3f3-3333-1111-1111-111111111111', 'Student Pass', 1999.00, 200, 200, CURRENT_TIMESTAMP, 'e1e1e1e1-3333-3333-3333-333333333333'),
('f3f3f3f3-3334-1111-1111-111111111111', 'Virtual Access', 999.00, 1000, 1000, CURRENT_TIMESTAMP, 'e1e1e1e1-3333-3333-3333-333333333333');

-- Healthcare Innovation Conference
INSERT INTO ticket_categories (id, name, price, total_seats, available_seats, created_at, event_id) VALUES
('f4f4f4f4-4441-1111-1111-111111111111', 'Professional', 12999.00, 300, 300, CURRENT_TIMESTAMP, 'e1e1e1e1-4444-4444-4444-444444444444'),
('f4f4f4f4-4442-1111-1111-111111111111', 'Academic', 6999.00, 150, 150, CURRENT_TIMESTAMP, 'e1e1e1e1-4444-4444-4444-444444444444'),
('f4f4f4f4-4443-1111-1111-111111111111', 'Virtual Only', 2999.00, 500, 500, CURRENT_TIMESTAMP, 'e1e1e1e1-4444-4444-4444-444444444444');

-- Chennai Cricket Championship Finals
INSERT INTO ticket_categories (id, name, price, total_seats, available_seats, created_at, event_id) VALUES
('f5f5f5f5-5551-1111-1111-111111111111', 'Pavilion', 3999.00, 200, 150, CURRENT_TIMESTAMP, 'e1e1e1e1-5555-5555-5555-555555555555'),
('f5f5f5f5-5552-1111-1111-111111111111', 'East Stand', 1999.00, 500, 400, CURRENT_TIMESTAMP, 'e1e1e1e1-5555-5555-5555-555555555555'),
('f5f5f5f5-5553-1111-1111-111111111111', 'West Stand', 1999.00, 500, 480, CURRENT_TIMESTAMP, 'e1e1e1e1-5555-5555-5555-555555555555'),
('f5f5f5f5-5554-1111-1111-111111111111', 'General', 799.00, 1000, 1000, CURRENT_TIMESTAMP, 'e1e1e1e1-5555-5555-5555-555555555555');

-- Marathon Chennai 2026
INSERT INTO ticket_categories (id, name, price, total_seats, available_seats, created_at, event_id) VALUES
('f6f6f6f6-6661-1111-1111-111111111111', 'Full Marathon', 1999.00, 500, 500, CURRENT_TIMESTAMP, 'e1e1e1e1-6666-6666-6666-666666666666'),
('f6f6f6f6-6662-1111-1111-111111111111', '10K Run', 799.00, 1000, 920, CURRENT_TIMESTAMP, 'e1e1e1e1-6666-6666-6666-666666666666'),
('f6f6f6f6-6663-1111-1111-111111111111', '5K Fun Run', 499.00, 2000, 2000, CURRENT_TIMESTAMP, 'e1e1e1e1-6666-6666-6666-666666666666');

-- Digital Marketing Masterclass
INSERT INTO ticket_categories (id, name, price, total_seats, available_seats, created_at, event_id) VALUES
('a7a7a7a7-7771-1111-1111-111111111111', 'Premium (with Materials)', 4999.00, 50, 45, CURRENT_TIMESTAMP, 'e1e1e1e1-7777-7777-7777-777777777777'),
('a7a7a7a7-7772-1111-1111-111111111111', 'Standard', 2999.00, 100, 100, CURRENT_TIMESTAMP, 'e1e1e1e1-7777-7777-7777-777777777777');

-- Photography Workshop
INSERT INTO ticket_categories (id, name, price, total_seats, available_seats, created_at, event_id) VALUES
('a8a8a8a8-8881-1111-1111-111111111111', 'Premium (with Equipment)', 5999.00, 20, 20, CURRENT_TIMESTAMP, 'e1e1e1e1-8888-8888-8888-888888888888'),
('a8a8a8a8-8882-1111-1111-111111111111', 'Standard (BYO Equipment)', 3499.00, 30, 28, CURRENT_TIMESTAMP, 'e1e1e1e1-8888-8888-8888-888888888888');

-- Shakespeare's Hamlet
INSERT INTO ticket_categories (id, name, price, total_seats, available_seats, created_at, event_id) VALUES
('a9a9a9a9-9991-1111-1111-111111111111', 'Stalls', 1999.00, 80, 80, CURRENT_TIMESTAMP, 'e1e1e1e1-9999-9999-9999-999999999999'),
('a9a9a9a9-9992-1111-1111-111111111111', 'Circle', 1299.00, 100, 100, CURRENT_TIMESTAMP, 'e1e1e1e1-9999-9999-9999-999999999999'),
('a9a9a9a9-9993-1111-1111-111111111111', 'Balcony', 799.00, 120, 120, CURRENT_TIMESTAMP, 'e1e1e1e1-9999-9999-9999-999999999999');

-- Chennai Food Festival 2026
INSERT INTO ticket_categories (id, name, price, total_seats, available_seats, created_at, event_id) VALUES
('aaaaaaaa-aaa1-1111-1111-111111111111', 'VIP Food Pass', 2999.00, 200, 200, CURRENT_TIMESTAMP, 'e1e1e1e1-aaaa-aaaa-aaaa-aaaaaaaaaaaa'),
('aaaaaaaa-aaa2-1111-1111-111111111111', 'General Entry', 499.00, 5000, 4800, CURRENT_TIMESTAMP, 'e1e1e1e1-aaaa-aaaa-aaaa-aaaaaaaaaaaa'),
('aaaaaaaa-aaa3-1111-1111-111111111111', 'Family Pass (4 people)', 1499.00, 1000, 1000, CURRENT_TIMESTAMP, 'e1e1e1e1-aaaa-aaaa-aaaa-aaaaaaaaaaaa');

-- Art & Design Expo 2026
INSERT INTO ticket_categories (id, name, price, total_seats, available_seats, created_at, event_id) VALUES
('bbbbbbbb-bbb1-1111-1111-111111111111', 'Collector Pass', 1999.00, 50, 50, CURRENT_TIMESTAMP, 'e1e1e1e1-bbbb-bbbb-bbbb-bbbbbbbbbbbb'),
('bbbbbbbb-bbb2-1111-1111-111111111111', 'Standard Entry', 499.00, 500, 500, CURRENT_TIMESTAMP, 'e1e1e1e1-bbbb-bbbb-bbbb-bbbbbbbbbbbb'),
('bbbbbbbb-bbb3-1111-1111-111111111111', 'Student Entry', 199.00, 300, 300, CURRENT_TIMESTAMP, 'e1e1e1e1-bbbb-bbbb-bbbb-bbbbbbbbbbbb');

-- Stand-Up Comedy Night
INSERT INTO ticket_categories (id, name, price, total_seats, available_seats, created_at, event_id) VALUES
('cccccccc-ccc1-1111-1111-111111111111', 'Front Row', 2499.00, 20, 15, CURRENT_TIMESTAMP, 'e1e1e1e1-cccc-cccc-cccc-cccccccccccc'),
('cccccccc-ccc2-1111-1111-111111111111', 'Premium', 1499.00, 100, 85, CURRENT_TIMESTAMP, 'e1e1e1e1-cccc-cccc-cccc-cccccccccccc'),
('cccccccc-ccc3-1111-1111-111111111111', 'Standard', 999.00, 200, 200, CURRENT_TIMESTAMP, 'e1e1e1e1-cccc-cccc-cccc-cccccccccccc');

-- =====================================================
-- 5. BOOKINGS (Sample bookings)
-- =====================================================

INSERT INTO bookings (id, booking_ref, quantity, status, booked_at, user_id, ticket_category_id) VALUES
-- Alice Johnson bookings
('eeeeeeee-1111-1111-1111-111111111111', 'BOOK-A1B2C3D4', 2, 'CONFIRMED', CURRENT_TIMESTAMP, 'd1d1d1d1-1111-1111-1111-111111111111', 'f1f1f1f1-1114-1111-1111-111111111111'),
('eeeeeeee-1112-1111-1111-111111111111', 'BOOK-E5F6G7H8', 1, 'CONFIRMED', CURRENT_TIMESTAMP, 'd1d1d1d1-1111-1111-1111-111111111111', 'f6f6f6f6-6662-1111-1111-111111111111'),

-- Bob Martinez bookings
('eeeeeeee-2221-1111-1111-111111111111', 'BOOK-I9J0K1L2', 3, 'CONFIRMED', CURRENT_TIMESTAMP, 'd1d1d1d1-2222-2222-2222-222222222222', 'f5f5f5f5-5552-1111-1111-111111111111'),
('eeeeeeee-2222-1111-1111-111111111111', 'BOOK-M3N4O5P6', 2, 'CANCELLED', CURRENT_TIMESTAMP, 'd1d1d1d1-2222-2222-2222-222222222222', 'cccccccc-ccc2-1111-1111-111111111111'),

-- Carol Davis bookings
('eeeeeeee-3331-1111-1111-111111111111', 'BOOK-Q7R8S9T0', 1, 'CONFIRMED', CURRENT_TIMESTAMP, 'd1d1d1d1-3333-3333-3333-333333333333', 'f3f3f3f3-3331-1111-1111-111111111111'),
('eeeeeeee-3332-1111-1111-111111111111', 'BOOK-U1V2W3X4', 1, 'CONFIRMED', CURRENT_TIMESTAMP, 'd1d1d1d1-3333-3333-3333-333333333333', 'a7a7a7a7-7771-1111-1111-111111111111'),

-- Daniel Kumar bookings
('eeeeeeee-4441-1111-1111-111111111111', 'BOOK-Y5Z6A7B8', 4, 'CONFIRMED', CURRENT_TIMESTAMP, 'd1d1d1d1-4444-4444-4444-444444444444', 'aaaaaaaa-aaa3-1111-1111-111111111111'),

-- Emily Brown bookings
('eeeeeeee-5551-1111-1111-111111111111', 'BOOK-C9D0E1F2', 2, 'CONFIRMED', CURRENT_TIMESTAMP, 'd1d1d1d1-5555-5555-5555-555555555555', 'a8a8a8a8-8882-1111-1111-111111111111'),
('eeeeeeee-5552-1111-1111-111111111111', 'BOOK-G3H4I5J6', 1, 'CONFIRMED', CURRENT_TIMESTAMP, 'd1d1d1d1-5555-5555-5555-555555555555', 'cccccccc-ccc1-1111-1111-111111111111'),

-- Frank Liu bookings
('eeeeeeee-6661-1111-1111-111111111111', 'BOOK-K7L8M9N0', 2, 'CONFIRMED', CURRENT_TIMESTAMP, 'd1d1d1d1-6666-6666-6666-666666666666', 'f5f5f5f5-5551-1111-1111-111111111111'),

-- Grace Lee bookings
('eeeeeeee-7771-1111-1111-111111111111', 'BOOK-O1P2Q3R4', 1, 'CONFIRMED', CURRENT_TIMESTAMP, 'd1d1d1d1-7777-7777-7777-777777777777', 'f3f3f3f3-3332-1111-1111-111111111111');

-- =====================================================
-- DATA VERIFICATION QUERIES
-- =====================================================

-- Uncomment to verify data:
-- SELECT 'Event Categories' as table_name, COUNT(*) as record_count FROM event_categories
-- UNION ALL
-- SELECT 'Users', COUNT(*) FROM users
-- UNION ALL
-- SELECT 'Events', COUNT(*) FROM events
-- UNION ALL
-- SELECT 'Ticket Categories', COUNT(*) FROM ticket_categories
-- UNION ALL
-- SELECT 'Bookings', COUNT(*) FROM bookings;

-- =====================================================
-- SUMMARY
-- =====================================================
-- Event Categories: 8
-- Users: 15 (3 Admins, 5 Organisers, 7 Attendees)
-- Events: 12 (covering all categories)
-- Ticket Categories: 40+ (multiple per event)
-- Bookings: 12 (sample bookings)
-- Default Password: Password@1
-- =====================================================
