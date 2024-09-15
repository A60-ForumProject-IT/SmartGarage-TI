-- Insert into avatars
INSERT INTO avatars (id, avatar_url) VALUES (1, '/images/DefaultUserAvatar.jpg');

-- Insert into roles
INSERT INTO roles (id, role_name) VALUES
                                      (1, 'Customer'),
                                      (2, 'Employee'),
                                      (3, 'Mechanic');

-- Insert into base_services
INSERT INTO base_services (Id, name) VALUES
                                         (1, 'Engine Diagnostics'),
                                         (2, 'Lube, Oil and Filters'),
                                         (3, 'Belts and Hoses'),
                                         (4, 'Air Conditioning'),
                                         (5, 'Brake Repair'),
                                         (6, 'Tire and Wheel Services');

-- Insert into brands
INSERT INTO brands (id, brand_name) VALUES
                                        (1, 'Audi'),
                                        (2, 'Porsche'),
                                        (3, 'Volkswagen');

-- Insert into models (approx. 40 models total)
INSERT INTO models (id, model_name) VALUES
                                        (1, 'A4'), (2, 'A6'), (3, 'Q5'), (4, 'Q7'),
                                        (5, 'TT'), (6, 'R8'), (7, 'S3'), (8, 'S4'),
                                        (9, 'S5'), (10, 'Q3'), (11, 'A1'), (12, 'Q8'),
                                        (13, '911'), (14, 'Cayenne'), (15, 'Macan'),
                                        (16, 'Panamera'), (17, 'Taycan'), (18, 'Boxster'),
                                        (19, 'Cayman'), (20, '718'), (21, '918 Spyder'),
                                        (22, 'Carrera'), (23, 'Targa'), (24, 'GT3'),
                                        (25, 'Golf'), (26, 'Passat'), (27, 'Tiguan'),
                                        (28, 'Touareg'), (29, 'Polo'), (30, 'Beetle'),
                                        (31, 'Jetta'), (32, 'Arteon'), (33, 'ID.4'),
                                        (34, 'T-Roc'), (35, 'Up'), (36, 'Scirocco'),
                                        (37, 'Sharan'), (38, 'Transporter'), (39, 'Amarok');

-- Insert into engines (approx. 40 types)
INSERT INTO engines (id, engine_type) VALUES
                                          (1, '1.6 TDI'), (2, '2.0 TDI'), (3, '3.0 TDI'), (4, '2.0 TFSI'),
                                          (5, '3.0 TFSI'), (6, '4.0 TFSI'), (7, '5.2 FSI V10'), (8, '2.0 TSI'),
                                          (9, '3.0 V6'), (10, '4.0 V8'), (11, '2.0 Boxer'), (12, '3.0 Boxer'),
                                          (13, '3.6 Boxer'), (14, '4.0 Boxer'), (15, '4.8 V8'), (16, '5.0 V10'),
                                          (17, '2.0 Hybrid'), (18, '3.0 Hybrid'), (19, 'Electric'), (20, '2.5 TDI'),
                                          (21, '1.4 TSI'), (22, '1.8 TSI'), (23, '3.6 V6'), (24, '3.8 V8'),
                                          (25, '1.0 TSI'), (26, '2.0 TSI Hybrid'), (27, '6.0 W12'), (28, '2.7 BiTurbo'),
                                          (29, '5.0 TFSI V8'), (30, '2.9 V6'), (31, '4.0 W12'), (32, '2.5 Turbo'),
                                          (33, '2.7 Turbo'), (34, '4.5 V8'), (35, '1.6 CRDi'), (36, '1.4 TDI'),
                                          (37, '2.0 BiTDI'), (38, '1.0 MPI'), (39, '1.5 TFSI');

-- Insert into years (approx. 30 years)
INSERT INTO years (id, year) VALUES
                                 (1, 1995), (2, 1996), (3, 1997), (4, 1998), (5, 1999), (6, 2000),
                                 (7, 2001), (8, 2002), (9, 2003), (10, 2004), (11, 2005), (12, 2006),
                                 (13, 2007), (14, 2008), (15, 2009), (16, 2010), (17, 2011), (18, 2012),
                                 (19, 2013), (20, 2014), (21, 2015), (22, 2016), (23, 2017), (24, 2018),
                                 (25, 2019), (26, 2020), (27, 2021), (28, 2022), (29, 2023), (30, 2024);

-- Insert into users (20 users)
INSERT INTO users (id, username, first_name, last_name, email, password, role_id, phone_number) VALUES
                                                                                                    (1, 'alex_rider', 'Alex', 'Rider', 'alex.rider@gmail.com', 'password123%D', 1, '0877000001'),
                                                                                                    (2, 'bella_harper', 'Bella', 'Harper', 'bella.harper@yahoo.com', 'password123%D', 1, '0877000002'),
                                                                                                    (3, 'chris_oakley', 'Chris', 'Oakley', 'chris.oakley@abv.bg', 'password123%D', 1, '0877000003'),
                                                                                                    (4, 'diana_smith', 'Diana', 'Smith', 'diana.smith@gmail.com', 'password123%D', 1, '0877000004'),
                                                                                                    (5, 'ethan_jones', 'Ethan', 'Jones', 'ethan.jones@yahoo.com', 'password123%D', 1, '0877000005'),
                                                                                                    (6, 'felix_jackson', 'Felix', 'Jackson', 'felix.jackson@abv.bg', 'password123%D', 2, '0877000006'),
                                                                                                    (7, 'george_hill', 'George', 'Hill', 'george.hill@gmail.com', 'password123%D', 2, '0877000007'),
                                                                                                    (8, 'hannah_white', 'Hannah', 'White', 'hannah.white@yahoo.com', 'password123%D', 2, '0877000008'),
                                                                                                    (9, 'ian_black', 'Ian', 'Black', 'ian.black@abv.bg', 'password123%D', 2, '0877000009'),
                                                                                                    (10, 'julia_grey', 'Julia', 'Grey', 'julia.grey@gmail.com', 'password123%D', 3, '0877000010'),
                                                                                                    (11, 'kevin_brown', 'Kevin', 'Brown', 'kevin.brown@yahoo.com', 'password123%D', 3, '0877000011'),
                                                                                                    (12, 'lucas_morgan', 'Lucas', 'Morgan', 'lucas.morgan@abv.bg', 'password123%D', 3, '0877000012'),
                                                                                                    (13, 'mia_clark', 'Mia', 'Clark', 'mia.clark@gmail.com', 'password123%D', 1, '0877000013'),
                                                                                                    (14, 'noah_lee', 'Noah', 'Lee', 'noah.lee@yahoo.com', 'password123%D', 1, '0877000014'),
                                                                                                    (15, 'oliver_wilson', 'Oliver', 'Wilson', 'oliver.wilson@abv.bg', 'password123%D', 1, '0877000015'),
                                                                                                    (16, 'peter_moore', 'Peter', 'Moore', 'peter.moore@gmail.com', 'password123%D', 1, '0877000016'),
                                                                                                    (17, 'quinn_walker', 'Quinn', 'Walker', 'quinn.walker@yahoo.com', 'password123%D', 1, '0877000017'),
                                                                                                    (18, 'rachel_smith', 'Rachel', 'Smith', 'rachel.smith@abv.bg', 'password123%D', 1, '0877000018'),
                                                                                                    (19, 'sophia_thomas', 'Sophia', 'Thomas', 'sophia.thomas@gmail.com', 'password123%D', 1, '0877000019'),
                                                                                                    (20, 'tyler_king', 'Tyler', 'King', 'tyler.king@yahoo.com', 'password123%D', 1, '0877000020'),
                                                                                                    (21, 'ilian19', 'Ilian', 'Karagyozov', 'ikaragyozov19@gmail.com', 'password123%D', 2, '0877000021'),
                                                                                                    (22, 'Annihilati0N', 'Todor', 'Raynov', 't.raynov@abv.bg', 'password123%D', 2, '0877000022');

-- Insert into users_avatars
INSERT INTO users_avatars (user_id, avatar_id) VALUES
                                                   (1, 1), (2, 1), (3, 1), (4, 1), (5, 1), (6, 1), (7, 1), (8, 1), (9, 1), (10, 1),
                                                   (11, 1), (12, 1), (13, 1), (14, 1), (15, 1), (16, 1), (17, 1), (18, 1), (19, 1), (20, 1), (21, 1), (22, 1);

-- Insert into vehicles (at least 40 vehicles with realistic combinations)
INSERT INTO vehicles (id, brand_id, model_id, year_id, engine_type_id, is_deleted) VALUES
                                                                                       (1, 1, 1, 16, 2, 0), (2, 1, 3, 25, 9, 0), (3, 1, 4, 18, 5, 0),
                                                                                       (4, 1, 2, 22, 3, 0), (5, 1, 10, 14, 8, 0), (6, 1, 8, 28, 13, 0),
                                                                                       (7, 1, 9, 19, 12, 0), (8, 1, 5, 23, 6, 0), (9, 2, 13, 26, 14, 0),
                                                                                       (10, 2, 14, 24, 15, 0), (11, 2, 16, 20, 16, 0), (12, 2, 15, 17, 20, 0),
                                                                                       (13, 2, 17, 29, 11, 0), (14, 2, 19, 12, 19, 0), (15, 2, 18, 11, 14, 0),
                                                                                       (16, 2, 20, 27, 18, 0), (17, 2, 22, 30, 17, 0), (18, 2, 24, 21, 10, 0),
                                                                                       (19, 3, 25, 13, 21, 0), (20, 3, 27, 6, 24, 0), (21, 3, 26, 4, 25, 0),
                                                                                       (22, 3, 28, 8, 22, 0), (23, 3, 30, 2, 1, 0), (24, 3, 29, 9, 7, 0),
                                                                                       (25, 3, 31, 15, 23, 0), (26, 3, 33, 29, 32, 0), (27, 3, 34, 5, 35, 0),
                                                                                       (28, 3, 32, 10, 38, 0), (29, 3, 35, 3, 39, 0), (30, 3, 36, 1, 34, 0);

-- Insert into clients_cars (at least 25 client cars with valid VINs and license plates)
INSERT INTO clients_cars (id, vin, plate, owner, vehicle_id, is_deleted) VALUES
                                                                             (1, 'WAUZZZ8P4AA000001', 'A1234BC', 1, 1, 0),
                                                                             (2, 'WAUZZZ8P4AA000002', 'B2345CH', 2, 2, 0),
                                                                             (3, 'WAUZZZ8P4AA000003', 'CH3456AE', 3, 3, 0),
                                                                             (4, 'WAUZZZ8P4AA000004', 'E4567PA', 4, 4, 0),
                                                                             (5, 'WAUZZZ8P4AA000005', 'KH5678BH', 5, 5, 0),
                                                                             (6, 'WAUZZZ8P4AA000006', 'A6789TM', 6, 6, 0),
                                                                             (7, 'WAUZZZ8P4AA000007', 'M7890CO', 7, 7, 0),
                                                                             (8, 'WAUZZZ8P4AA000008', 'CH8901BA', 8, 8, 0),
                                                                             (9, 'WAUZZZ8P4AA000009', 'K9012KM', 9, 9, 0),
                                                                             (10, 'WAUZZZ8P4AA000010', 'P1235AP', 10, 10, 0),
                                                                             (11, 'WAUZZZ8P4AA000011', 'CO1234HK', 11, 11, 0),
                                                                             (12, 'WAUZZZ8P4AA000012', 'CA345OP', 12, 12, 0),
                                                                             (13, 'WAUZZZ8P4AA000013', 'B3456PK', 13, 13, 0),
                                                                             (14, 'WAUZZZ8P4AA000014', 'PK4567EH', 14, 14, 0),
                                                                             (15, 'WAUZZZ8P4AA000015', 'EH5678MA', 15, 15, 0),
                                                                             (16, 'WAUZZZ8P4AA000016', 'TX6789TA', 16, 16, 0),
                                                                             (17, 'WAUZZZ8P4AA000017', 'PB7890BB', 17, 17, 0),
                                                                             (18, 'WAUZZZ8P4AA000018', 'KH8901AM', 18, 18, 0),
                                                                             (19, 'WAUZZZ8P4AA000019', 'PP9012AC', 19, 19, 0),
                                                                             (20, 'WAUZZZ8P4AA000020', 'H0123PO', 20, 20, 0),
                                                                             (21, 'WAUZZZ8P4AA000021', 'EB1234KK', 1, 21, 0),
                                                                             (22, 'WAUZZZ8P4AA000022', 'X2345MM', 2, 22, 0),
                                                                             (23, 'WAUZZZ8P4AA000023', 'Y3456CA', 3, 23, 0),
                                                                             (24, 'WAUZZZ8P4AA000024', 'CC4567AB', 4, 24, 0),
                                                                             (25, 'WAUZZZ8P4AA000025', 'OB5678BC', 5, 25, 0);

-- Insert into services (36 services, 6 types per base service)
INSERT INTO services (id, name, price, is_deleted, base_service_id) VALUES
                                                                        (1, 'Basic Engine Diagnostics', 100.00, 0, 1),
                                                                        (2, 'Advanced Engine Diagnostics', 200.00, 0, 1),
                                                                        (3, 'Complete Engine Diagnostics', 300.00, 0, 1),
                                                                        (4, 'Quick Engine Check', 80.00, 0, 1),
                                                                        (5, 'Engine Fault Finding', 120.00, 0, 1),
                                                                        (6, 'Engine Performance Test', 150.00, 0, 1),
                                                                        (7, 'Basic Lube, Oil and Filters', 50.00, 0, 2),
                                                                        (8, 'Advanced Lube, Oil and Filters', 90.00, 0, 2),
                                                                        (9, 'Premium Lube Service', 120.00, 0, 2),
                                                                        (10, 'Oil Change and Inspection', 70.00, 0, 2),
                                                                        (11, 'Synthetic Oil Change', 110.00, 0, 2),
                                                                        (12, 'Complete Lube Service', 130.00, 0, 2),
                                                                        (13, 'Basic Belts and Hoses Replacement', 80.00, 0, 3),
                                                                        (14, 'Advanced Belts and Hoses Replacement', 150.00, 0, 3),
                                                                        (15, 'Complete Belts and Hoses Service', 200.00, 0, 3),
                                                                        (16, 'Timing Belt Replacement', 250.00, 0, 3),
                                                                        (17, 'Accessory Belt Replacement', 90.00, 0, 3),
                                                                        (18, 'Hose Leak Repair', 60.00, 0, 3),
                                                                        (19, 'Basic Air Conditioning Service', 70.00, 0, 4),
                                                                        (20, 'Advanced Air Conditioning Service', 120.00, 0, 4),
                                                                        (21, 'Complete Air Conditioning Service', 180.00, 0, 4),
                                                                        (22, 'AC System Recharge', 100.00, 0, 4),
                                                                        (23, 'AC Filter Replacement', 90.00, 0, 4),
                                                                        (24, 'AC Leak Repair', 110.00, 0, 4),
                                                                        (25, 'Basic Brake Repair', 120.00, 0, 5),
                                                                        (26, 'Advanced Brake Repair', 180.00, 0, 5),
                                                                        (27, 'Complete Brake Service', 240.00, 0, 5),
                                                                        (28, 'Brake Pad Replacement', 140.00, 0, 5),
                                                                        (29, 'Brake Fluid Change', 90.00, 0, 5),
                                                                        (30, 'Brake System Inspection', 70.00, 0, 5),
                                                                        (31, 'Basic Tire and Wheel Service', 60.00, 0, 6),
                                                                        (32, 'Advanced Tire and Wheel Service', 100.00, 0, 6),
                                                                        (33, 'Complete Tire and Wheel Service', 150.00, 0, 6),
                                                                        (34, 'Tire Rotation and Balancing', 80.00, 0, 6),
                                                                        (35, 'Wheel Alignment', 110.00, 0, 6),
                                                                        (36, 'Tire Replacement', 130.00, 0, 6);

-- Insert into orders (at least 30 orders with valid dates and statuses)
INSERT INTO orders (id, client_car_id, status, order_date) VALUES
                                                               (1, 1, 'IN_PROGRESS', '2023-08-01'),
                                                               (2, 2, 'NOT_STARTED', '2023-08-05'),
                                                               (3, 3, 'READY_FOR_PICKUP', '2023-08-10'),
                                                               (4, 4, 'IN_PROGRESS', '2023-08-15'),
                                                               (5, 5, 'NOT_STARTED', '2023-08-20'),
                                                               (6, 6, 'READY_FOR_PICKUP', '2023-08-25'),
                                                               (7, 7, 'IN_PROGRESS', '2023-08-30'),
                                                               (8, 8, 'NOT_STARTED', '2023-09-01'),
                                                               (9, 9, 'READY_FOR_PICKUP', '2023-09-05'),
                                                               (10, 10, 'IN_PROGRESS', '2023-09-10'),
                                                               (11, 11, 'NOT_STARTED', '2023-09-15'),
                                                               (12, 12, 'READY_FOR_PICKUP', '2023-09-20'),
                                                               (13, 13, 'IN_PROGRESS', '2023-09-25'),
                                                               (14, 14, 'READY_FOR_PICKUP', '2023-09-30'),
                                                               (15, 15, 'READY_FOR_PICKUP', '2023-10-01'),
                                                               (16, 16, 'IN_PROGRESS', '2023-10-05'),
                                                               (17, 17, 'NOT_STARTED', '2023-10-10'),
                                                               (18, 18, 'READY_FOR_PICKUP', '2023-10-15'),
                                                               (19, 19, 'IN_PROGRESS', '2023-10-20'),
                                                               (20, 20, 'READY_FOR_PICKUP', '2023-10-25'),
                                                               (21, 21, 'READY_FOR_PICKUP', '2023-11-01'),
                                                               (22, 22, 'IN_PROGRESS', '2023-11-05'),
                                                               (23, 23, 'NOT_STARTED', '2023-11-10'),
                                                               (24, 24, 'READY_FOR_PICKUP', '2023-11-15'),
                                                               (25, 25, 'IN_PROGRESS', '2023-11-20'),
                                                               (26, 20, 'NOT_STARTED', '2023-11-20'),
                                                               (27, 14, 'NOT_STARTED', '2023-11-20');


-- Insert into cars_services (at least 30 logs with calculated prices)
INSERT INTO cars_services (clients_cars_id, service_id, service_date, calculated_price, order_id) VALUES
                                                                                                      (1, 1, '2023-08-01', 100.00, 1),
                                                                                                      (2, 2, '2023-08-05', 200.00, 2),
                                                                                                      (3, 3, '2023-08-10', 300.00 * 1.3, 3), -- Year between 2000-2009
                                                                                                      (4, 4, '2023-08-15', 80.00 * 1.5, 4), -- Year between 2010-2019
                                                                                                      (5, 5, '2023-08-20', 120.00 * 1.7, 5), -- Year between 2020-present
                                                                                                      (6, 6, '2023-08-25', 150.00 * 1.7, 6),
                                                                                                      (7, 7, '2023-08-30', 50.00, 7),
                                                                                                      (8, 8, '2023-09-01', 90.00, 8),
                                                                                                      (9, 9, '2023-09-05', 120.00 * 1.3, 9),
                                                                                                      (10, 10, '2023-09-10', 70.00 * 1.5, 10),
                                                                                                      (11, 11, '2023-09-15', 110.00 * 1.7, 11),
                                                                                                      (12, 12, '2023-09-20', 130.00 * 1.7, 12),
                                                                                                      (13, 13, '2023-09-25', 80.00, 13),
                                                                                                      (14, 14, '2023-09-30', 150.00, 14),
                                                                                                      (15, 15, '2023-10-01', 200.00 * 1.3, 15),
                                                                                                      (16, 16, '2023-10-05', 250.00 * 1.5, 16),
                                                                                                      (17, 17, '2023-10-10', 90.00 * 1.7, 17),
                                                                                                      (18, 18, '2023-10-15', 60.00 * 1.7, 18),
                                                                                                      (19, 19, '2023-10-20', 70.00, 19),
                                                                                                      (20, 20, '2023-10-25', 120.00, 20),
                                                                                                      (21, 21, '2023-11-01', 180.00 * 1.3, 21),
                                                                                                      (22, 22, '2023-11-05', 100.00 * 1.5, 22),
                                                                                                      (23, 23, '2023-11-10', 90.00 * 1.7, 23),
                                                                                                      (24, 24, '2023-11-15', 110.00 * 1.7, 24),
                                                                                                      (25, 25, '2023-11-20', 120.00, 25);
