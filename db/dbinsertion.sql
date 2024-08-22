INSERT INTO roles (role_name) VALUES ('Customer'), ('Employee');

INSERT INTO users (username, first_name, last_name, email, password, role_id, phone_number) VALUES
                                                                                                ('john_doe', 'John', 'Doe', 'john.doe@example.com', 'password123', 1, '5551234567'),
                                                                                                ('jane_smith', 'Jane', 'Smith', 'jane.smith@example.com', 'password123', 1, '5559876543'),
                                                                                                ('michael_brown', 'Michael', 'Brown', 'michael.brown@example.com', 'password123', 2, '5558765432'),
                                                                                                ('emily_jones', 'Emily', 'Jones', 'emily.jones@example.com', 'password123', 1, '5557654321'),
                                                                                                ('daniel_taylor', 'Daniel', 'Taylor', 'daniel.taylor@example.com', 'password123', 1, '5556543210'),
                                                                                                ('laura_white', 'Laura', 'White', 'laura.white@example.com', 'password123', 2, '5555432109'),
                                                                                                ('kevin_clark', 'Kevin', 'Clark', 'kevin.clark@example.com', 'password123', 1, '5554321098'),
                                                                                                ('olivia_king', 'Olivia', 'King', 'olivia.king@example.com', 'password123', 1, '5553210987'),
                                                                                                ('ryan_scott', 'Ryan', 'Scott', 'ryan.scott@example.com', 'password123', 2, '5552109876'),
                                                                                                ('sophia_lewis', 'Sophia', 'Lewis', 'sophia.lewis@example.com', 'password123', 1, '5551098765');

INSERT INTO brands (brand_name) VALUES ('Audi'), ('Porsche'), ('Volkswagen');

INSERT INTO models (model_name) VALUES ('A4'), ('911'), ('Golf'), ('Q7'), ('Cayenne'), ('Passat');

INSERT INTO years (year) VALUES
                             (2002), (2003), (2004), (2005),
                             (2006), (2007), (2008), (2009), (2010), (2011), (2012), (2013), (2014), (2015),
                             (2016), (2017), (2018), (2019), (2020), (2021), (2022), (2023), (2024);

INSERT INTO engines (engine_type) VALUES ('1.9 TDI');
INSERT INTO engines (engine_type) VALUES ('2.0 TDI');
INSERT INTO engines (engine_type) VALUES ('1.8 T');
INSERT INTO engines (engine_type) VALUES ('2.0 TFSI');
INSERT INTO engines (engine_type) VALUES ('3.0 TFSI');
INSERT INTO engines (engine_type) VALUES ('2.5 TDI');
INSERT INTO engines (engine_type) VALUES ('3.6 VR6');
INSERT INTO engines (engine_type) VALUES ('4.2 V8');
INSERT INTO engines (engine_type) VALUES ('2.9 V6 Biturbo');
INSERT INTO engines (engine_type) VALUES ('3.0 V6 TDI');
INSERT INTO engines (engine_type) VALUES ('4.0 TFSI');
INSERT INTO engines (engine_type) VALUES ('2.0 TSI');
INSERT INTO engines (engine_type) VALUES ('3.0 TDI');
INSERT INTO engines (engine_type) VALUES ('4.0 V8 Biturbo');
INSERT INTO engines (engine_type) VALUES ('5.0 W12');

INSERT INTO vehicles (brand_id, model_id, year_id, engine_type_id) VALUES
                                                                       (1, 1, 10, 1), -- Audi A4 (2004)
                                                                       (2, 2, 15, 9), -- Porsche 911 (2008)
                                                                       (3, 3, 20, 2), -- Volkswagen Golf (2006)
                                                                       (1, 4, 14, 11), -- Audi Q7 (2005)
                                                                       (2, 5, 13, 14), -- Porsche Cayenne (2009)
                                                                       (3, 6, 8, 13); -- Volkswagen Passat (2007)

INSERT INTO clients_cars (vin, plate, owner, vehicle_id) VALUES
                                                             ('WAULC58E05A000001', 'B1234MO', 1, 1),
                                                             ('WP0AA2A92FS000002', 'B5678AB', 2, 2),
                                                             ('WVWZZZ1JZXW000003', 'C1234PK', 3, 3),
                                                             ('WA1YD64B45N000004', 'C5678PA', 4, 4),
                                                             ('WP1AB29P39L000005', 'A1234AT', 5, 5),
                                                             ('WVWHV7AJ8AW000006', 'A5678AK', 6, 6);

INSERT INTO services (name, price) VALUES
                                       ('Brake Change', 150.00),
                                       ('Oil Change', 75.00),
                                       ('Tire Rotation', 50.00),
                                       ('Battery Replacement', 120.00),
                                       ('Engine Diagnostic', 200.00),
                                       ('Transmission Repair', 300.00),
                                       ('AC Repair', 180.00),
                                       ('Windshield Replacement', 250.00),
                                       ('Paint Job', 400.00),
                                       ('Detailing', 100.00);

INSERT INTO cars_services (clients_cars_id, service_id, service_date) VALUES
                                                                          (1, 1, '2024-01-15'),
                                                                          (1, 2, '2024-02-20'),
                                                                          (2, 3, '2024-03-10'),
                                                                          (3, 4, '2024-04-05'),
                                                                          (4, 5, '2024-05-18'),
                                                                          (5, 6, '2024-06-22'),
                                                                          (6, 7, '2024-07-30'),
                                                                          (1, 8, '2024-08-12'),
                                                                          (2, 9, '2024-09-15'),
                                                                          (3, 10, '2024-10-20');