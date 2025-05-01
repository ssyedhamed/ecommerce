INSERT INTO categories (category_id, category_name) VALUES (1, 'Mens T-Shirts');
INSERT INTO categories (category_id, category_name) VALUES (2, 'Smartphones');
INSERT INTO categories (category_id, category_name) VALUES (3, 'Apparel');
INSERT INTO categories (category_id, category_name) VALUES (4, 'Home Appliances');
INSERT INTO categories (category_id, category_name) VALUES (5, 'Toys');
INSERT INTO categories (category_id, category_name) VALUES (6, 'Furniture');
INSERT INTO categories (category_id, category_name) VALUES (7, 'Books');
INSERT INTO categories (category_id, category_name) VALUES (8, 'Sports Equipment');
INSERT INTO categories (category_id, category_name) VALUES (9, 'Beauty Products');
INSERT INTO categories (category_id, category_name) VALUES (10, 'Automotive');
INSERT INTO categories (category_id, category_name) VALUES (11, 'Outdoor Gear');
INSERT INTO categories (category_id, category_name) VALUES (12, 'Electronics');
INSERT INTO categories (category_id, category_name) VALUES (13, 'Kitchen Appliances');
INSERT INTO categories (category_id, category_name) VALUES (14, 'Baby Products');
INSERT INTO categories (category_id, category_name) VALUES (15, 'Health & Fitness');
INSERT INTO categories (category_id, category_name) VALUES (16, 'Garden & Outdoor');
INSERT INTO categories (category_id, category_name) VALUES (17, 'Pet Supplies');
INSERT INTO categories (category_id, category_name) VALUES (18, 'Office Supplies');
INSERT INTO categories (category_id, category_name) VALUES (19, 'Jewelry & Watches');
INSERT INTO categories (category_id, category_name) VALUES (20, 'Travel & Luggage');
INSERT INTO categories (category_id, category_name) VALUES (21, 'Musical Instruments');
INSERT INTO categories (category_id, category_name) VALUES (22, 'Crafts & Hobbies');
INSERT INTO categories (category_id, category_name) VALUES (23, 'Collectibles & Memorabilia');
INSERT INTO categories (category_id, category_name) VALUES (24, 'Art & Decor');
INSERT INTO categories (category_id, category_name) VALUES (25, 'Food & Beverages');
INSERT INTO categories (category_id, category_name) VALUES (26, 'Stationery & Gift Wrapping');
INSERT INTO categories (category_id, category_name) VALUES (27, 'Electrical & Lighting');
INSERT INTO categories (category_id, category_name) VALUES (28, 'DIY & Tools');
INSERT INTO categories (category_id, category_name) VALUES (29, 'Party Supplies');
INSERT INTO categories (category_id, category_name) VALUES (30, 'Educational Toys');



INSERT INTO products (product_name, description, quantity, price, discount, special_price, product_image, category_id) VALUES
('Classic Cotton Tee', 'Soft cotton t-shirt for everyday wear', 100, 20.00, 10.00, 18.00, 'default.png', 1),
('V-Neck Sports Tee', 'Lightweight and breathable v-neck t-shirt', 80, 25.00, 15.00, 21.25, 'default.png', 1),
('Graphic Print Tee', 'Trendy t-shirt with unique graphic print', 120, 30.00, 20.00, 24.00, 'default.png', 1),
('Slim Fit Polo', 'Casual yet stylish polo shirt', 75, 35.00, 10.00, 31.50, 'default.png', 1),
('Striped Crew Neck', 'Classic striped design crew neck t-shirt', 95, 22.00, 5.00, 20.90, 'default.png', 1),

('Smartphone X1', 'Latest smartphone with 5G and AI camera', 50, 699.99, 50.00, 649.99, 'default.png', 2),
('Budget Phone A2', 'Affordable smartphone with great battery life', 80, 199.99, 20.00, 179.99, 'default.png', 2),
('Gaming Phone G3', 'High-performance smartphone for gamers', 30, 899.99, 100.00, 799.99, 'default.png', 2),
('Flagship Pro Z', 'Premium flagship smartphone with 1TB storage', 20, 1299.99, 150.00, 1149.99, 'default.png', 2),
('Compact Mini Y', 'Small and lightweight smartphone for portability', 40, 399.99, 30.00, 369.99, 'default.png', 2),

('Denim Jacket', 'Stylish denim jacket for all seasons', 60, 60.00, 10.00, 54.00, 'default.png', 3),
('Hoodie Classic', 'Comfortable fleece hoodie', 90, 50.00, 15.00, 42.50, 'default.png', 3),
('Slim Fit Jeans', 'Trendy jeans with stretch fit', 70, 55.00, 5.00, 52.25, 'default.png', 3),
('Casual Blazer', 'Perfect for semi-formal occasions', 40, 85.00, 20.00, 68.00, 'default.png', 3),
('Leather Belt', 'Genuine leather belt with metal buckle', 120, 30.00, 5.00, 28.50, 'default.png', 3),

('Air Purifier Pro', 'HEPA filter air purifier for clean air', 40, 150.00, 20.00, 120.00, 'default.png', 4),
('Smart Vacuum X', 'Robot vacuum cleaner with AI navigation', 35, 250.00, 30.00, 175.00, 'default.png', 4),
('Microwave Oven', '700W microwave with smart defrost function', 50, 120.00, 10.00, 108.00, 'default.png', 4),
('Coffee Maker Elite', 'Automatic coffee maker with milk frother', 45, 80.00, 15.00, 68.00, 'default.png', 4),
('Dishwasher Supreme', 'Energy-efficient dishwasher with multiple modes', 25, 600.00, 50.00, 500.00, 'default.png', 4),

('Remote Control Car', 'High-speed RC car with rechargeable battery', 70, 40.00, 5.00, 38.00, 'default.png', 5),
('Dollhouse Deluxe', 'Wooden dollhouse with furniture set', 30, 70.00, 10.00, 63.00, 'default.png', 5),
('Puzzle Set 1000', 'Challenging 1000-piece puzzle for adults', 80, 25.00, 5.00, 23.75, 'default.png', 5),
('LEGO Builder Kit', 'Create amazing structures with 500+ LEGO pieces', 50, 50.00, 10.00, 45.00, 'default.png', 5),
('Action Figure Hero', 'Collectible superhero action figure', 100, 20.00, 3.00, 19.40, 'default.png', 5);


INSERT INTO users (email, password, first_name, last_name, phone, profile_image,
    account_non_locked, enabled, email_verified,
    deleted, deleted_at, created_at, updated_at,
    deactivated, deactivated_at, seller_application_status
) VALUES
('user1@example.com', '$2a$12$Qh.YDgSLUP5xT6i1ClpslOWiTySBDg6Fh3fMSi.py1OcoLDka4RHq', 'Alice', 'Wonder', '1234567890', 'user.png',
true, true, true, false, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false, NULL, 'NOT_REQUESTED'),

( 'user2@example.com', '$2a$12$Qh.YDgSLUP5xT6i1ClpslOWiTySBDg6Fh3fMSi.py1OcoLDka4RHq', 'Bob', 'Builder', '1234567891', 'user.png',
true, true, true, true, DATEADD('DAY', -5, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false, NULL, 'NOT_REQUESTED'),

('user3@example.com', '$2a$12$Qh.YDgSLUP5xT6i1ClpslOWiTySBDg6Fh3fMSi.py1OcoLDka4RHq', 'Charlie', 'Brown', '1234567892', 'user.png',
true, false, true, false, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true, DATEADD('DAY', -10, CURRENT_TIMESTAMP), 'NOT_REQUESTED'),

( 'user4@example.com', '$2a$12$Qh.YDgSLUP5xT6i1ClpslOWiTySBDg6Fh3fMSi.py1OcoLDka4RHq', 'Daisy', 'Duck', '1234567893', 'user.png',
false, true, true, false, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false, NULL, 'NOT_REQUESTED'),

( 'user5@example.com', '$2a$12$Qh.YDgSLUP5xT6i1ClpslOWiTySBDg6Fh3fMSi.py1OcoLDka4RHq', 'Evan', 'Peters', '1234567894', 'user.png',
true, true, true, true, DATEADD('DAY', -1, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false, NULL, 'NOT_REQUESTED'),

( 'user6@example.com', '$2a$12$Qh.YDgSLUP5xT6i1ClpslOWiTySBDg6Fh3fMSi.py1OcoLDka4RHq', 'Fiona', 'Shrek', '1234567895', 'user.png',
true, false, true, true, DATEADD('DAY', -3, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true, DATEADD('DAY', -2, CURRENT_TIMESTAMP), 'NOT_REQUESTED'),

( 'user7@example.com', '$2a$12$Qh.YDgSLUP5xT6i1ClpslOWiTySBDg6Fh3fMSi.py1OcoLDka4RHq', 'George', 'Clooney', '1234567896', 'user.png',
true, true, true, false, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false, NULL, 'NOT_REQUESTED'),

( 'deleted8@example.com', '$2a$12$Qh.YDgSLUP5xT6i1ClpslOWiTySBDg6Fh3fMSi.py1OcoLDka4RHq', 'Hannah', 'Montana', '1234567897', 'user.png',
false, false, true, true, DATEADD('DAY', -7, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false, NULL, 'NOT_REQUESTED'),

( 'user9@example.com', '$2a$12$Qh.YDgSLUP5xT6i1ClpslOWiTySBDg6Fh3fMSi.py1OcoLDka4RHq', 'Ivy', 'League', '1234567898', 'user.png',
true, false, true, false, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true, DATEADD('DAY', -15, CURRENT_TIMESTAMP), 'NOT_REQUESTED'),

( 'user10@example.com', '$2a$12$Qh.YDgSLUP5xT6i1ClpslOWiTySBDg6Fh3fMSi.py1OcoLDka4RHq', 'Jack', 'Sparrow', '1234567899', 'user.png',
true, true, true, false, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false, NULL, 'NOT_REQUESTED');

-- Permissions
INSERT INTO permissions (id, name) VALUES (1, 'MANAGE_PRODUCTS');
INSERT INTO permissions (id, name) VALUES (2, 'VIEW_ORDERS');
INSERT INTO permissions (id, name) VALUES (3, 'PLACE_ORDER');
INSERT INTO permissions (id, name) VALUES (4, 'WRITE_REVIEW');

-- Roles (fixed column name)
INSERT INTO roles (id, name, description) VALUES (1, 'ROLE_ADMIN', 'Default description for ROLE_ADMIN');
INSERT INTO roles (id, name, description) VALUES (2, 'ROLE_SELLER', 'Default description for ROLE_SELLER');
INSERT INTO roles (id, name, description) VALUES (3, 'ROLE_CUSTOMER', 'Default description for ROLE_CUSTOMER');

-- Role-Permission join table
INSERT INTO role_permission (role_id, permission_id) VALUES (1, 1);
INSERT INTO role_permission (role_id, permission_id) VALUES (1, 2);
INSERT INTO role_permission (role_id, permission_id) VALUES (1, 3);
INSERT INTO role_permission (role_id, permission_id) VALUES (1, 4);

INSERT INTO role_permission (role_id, permission_id) VALUES (2, 1);
INSERT INTO role_permission (role_id, permission_id) VALUES (2, 2);

INSERT INTO role_permission (role_id, permission_id) VALUES (3, 3);
INSERT INTO role_permission (role_id, permission_id) VALUES (3, 4);



INSERT INTO user_roles (user_id, role_id) VALUES (1, 1);
INSERT INTO user_roles (user_id, role_id) VALUES (1, 2);
INSERT INTO user_roles (user_id, role_id) VALUES (1, 3);
INSERT INTO user_roles (user_id, role_id) VALUES (2, 3);
INSERT INTO user_roles (user_id, role_id) VALUES (3, 3);
INSERT INTO user_roles (user_id, role_id) VALUES (4, 3);
INSERT INTO user_roles (user_id, role_id) VALUES (5, 3);
INSERT INTO user_roles (user_id, role_id) VALUES (6, 3);
INSERT INTO user_roles (user_id, role_id) VALUES (7, 2);
INSERT INTO user_roles (user_id, role_id) VALUES (8, 2);
INSERT INTO user_roles (user_id, role_id) VALUES (9, 1);
INSERT INTO user_roles (user_id, role_id) VALUES (10, 3);

--addresses
-- INSERT INTO addresses (id, street, city, state, postal_code, country, address_type, is_default, user_id) VALUES
-- (1,  '221B Baker Street', 'Mumbai', 'Maharashtra', '400001', 'India',  'HOME', true, 1),
-- (2,  '14 Park Avenue', 'Ahmedabad', 'Gujarat', '380015', 'India',  'BILLING', true, 2),
-- (3,  '33 MG Road', 'Bangalore', 'Karnataka', '560001', 'India',  'WORK', true, 3),
-- (4,  '7 Lake View', 'Chennai', 'Tamil Nadu', '600001', 'India',  'SHIPPING', true, 4),
-- (5,  '19 Hill Street', 'Kolkata', 'West Bengal', '700001', 'India',  'HOME', false, 5),
-- (6,  '55 Marine Drive', 'Kochi', 'Kerala', '682001', 'India',  'BILLING', true, 6),
-- (7,  '88 Residency Road', 'Pune', 'Maharashtra', '411001', 'India',  'HOME', true, 7),
-- (8,  '123 Carter Road', 'Mumbai', 'Maharashtra', '400050', 'India',  'WORK', true, 8),
-- (9,  '72 Fort Area', 'Delhi', 'Delhi', '110001', 'India',  'SHIPPING', false, 9),
-- (10,  '10 Rajpath', 'New Delhi', 'Delhi', '110011', 'India',  'HOME', true, 10);
-- ALTER TABLE addresses ALTER COLUMN id RESTART WITH 11;