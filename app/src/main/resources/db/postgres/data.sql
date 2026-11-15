INSERT INTO vets (first_name, last_name) SELECT 'James', 'Carter' WHERE NOT EXISTS (SELECT * FROM vets WHERE id=1);
INSERT INTO vets (first_name, last_name) SELECT 'Helen', 'Leary' WHERE NOT EXISTS (SELECT * FROM vets WHERE id=2);
INSERT INTO vets (first_name, last_name) SELECT 'Linda', 'Douglas' WHERE NOT EXISTS (SELECT * FROM vets WHERE id=3);
INSERT INTO vets (first_name, last_name) SELECT 'Rafael', 'Ortega' WHERE NOT EXISTS (SELECT * FROM vets WHERE id=4);
INSERT INTO vets (first_name, last_name) SELECT 'Henry', 'Stevens' WHERE NOT EXISTS (SELECT * FROM vets WHERE id=5);
INSERT INTO vets (first_name, last_name) SELECT 'Sharon', 'Jenkins' WHERE NOT EXISTS (SELECT * FROM vets WHERE id=6);

INSERT INTO specialties (name) SELECT 'radiology' WHERE NOT EXISTS (SELECT * FROM specialties WHERE name='radiology');
INSERT INTO specialties (name) SELECT 'surgery' WHERE NOT EXISTS (SELECT * FROM specialties WHERE name='surgery'); 
INSERT INTO specialties (name) SELECT 'dentistry' WHERE NOT EXISTS (SELECT * FROM specialties WHERE name='dentistry');

INSERT INTO vet_specialties VALUES (2, 1) ON CONFLICT (vet_id, specialty_id) DO NOTHING;
INSERT INTO vet_specialties VALUES (3, 2) ON CONFLICT (vet_id, specialty_id) DO NOTHING;
INSERT INTO vet_specialties VALUES (3, 3) ON CONFLICT (vet_id, specialty_id) DO NOTHING;
INSERT INTO vet_specialties VALUES (4, 2) ON CONFLICT (vet_id, specialty_id) DO NOTHING;
INSERT INTO vet_specialties VALUES (5, 1) ON CONFLICT (vet_id, specialty_id) DO NOTHING;

INSERT INTO types (name) SELECT 'cat' WHERE NOT EXISTS (SELECT * FROM types WHERE name='cat');
INSERT INTO types (name) SELECT 'dog' WHERE NOT EXISTS (SELECT * FROM types WHERE name='dog');
INSERT INTO types (name) SELECT 'lizard' WHERE NOT EXISTS (SELECT * FROM types WHERE name='lizard');
INSERT INTO types (name) SELECT 'snake' WHERE NOT EXISTS (SELECT * FROM types WHERE name='snake');
INSERT INTO types (name) SELECT 'bird' WHERE NOT EXISTS (SELECT * FROM types WHERE name='bird');
INSERT INTO types (name) SELECT 'hamster' WHERE NOT EXISTS (SELECT * FROM types WHERE name='hamster');

INSERT INTO owners (first_name, last_name, address, city, telephone) SELECT 'George', 'Franklin', '110 W. Liberty St.', 'Madison', '6085551023' WHERE NOT EXISTS (SELECT * FROM owners WHERE id=1);
INSERT INTO owners (first_name, last_name, address, city, telephone) SELECT 'Betty', 'Davis', '638 Cardinal Ave.', 'Sun Prairie', '6085551749' WHERE NOT EXISTS (SELECT * FROM owners WHERE id=2);
INSERT INTO owners (first_name, last_name, address, city, telephone) SELECT 'Eduardo', 'Rodriquez', '2693 Commerce St.', 'McFarland', '6085558763' WHERE NOT EXISTS (SELECT * FROM owners WHERE id=3);
INSERT INTO owners (first_name, last_name, address, city, telephone) SELECT 'Harold', 'Davis', '563 Friendly St.', 'Windsor', '6085553198' WHERE NOT EXISTS (SELECT * FROM owners WHERE id=4);
INSERT INTO owners (first_name, last_name, address, city, telephone) SELECT 'Peter', 'McTavish', '2387 S. Fair Way', 'Madison', '6085552765' WHERE NOT EXISTS (SELECT * FROM owners WHERE id=5);
INSERT INTO owners (first_name, last_name, address, city, telephone) SELECT 'Jean', 'Coleman', '105 N. Lake St.', 'Monona', '6085552654' WHERE NOT EXISTS (SELECT * FROM owners WHERE id=6);
INSERT INTO owners (first_name, last_name, address, city, telephone) SELECT 'Jeff', 'Black', '1450 Oak Blvd.', 'Monona', '6085555387' WHERE NOT EXISTS (SELECT * FROM owners WHERE id=7);
INSERT INTO owners (first_name, last_name, address, city, telephone) SELECT 'Maria', 'Escobito', '345 Maple St.', 'Madison', '6085557683' WHERE NOT EXISTS (SELECT * FROM owners WHERE id=8);
INSERT INTO owners (first_name, last_name, address, city, telephone) SELECT 'David', 'Schroeder', '2749 Blackhawk Trail', 'Madison', '6085559435' WHERE NOT EXISTS (SELECT * FROM owners WHERE id=9);
INSERT INTO owners (first_name, last_name, address, city, telephone) SELECT 'Carlos', 'Estaban', '2335 Independence La.', 'Waunakee', '6085555487' WHERE NOT EXISTS (SELECT * FROM owners WHERE id=10);

INSERT INTO pets (name, birth_date, type_id, owner_id) SELECT 'Leo', '2000-09-07', 1, 1 WHERE NOT EXISTS (SELECT * FROM pets WHERE id=1);
INSERT INTO pets (name, birth_date, type_id, owner_id) SELECT 'Basil', '2002-08-06', 6, 2 WHERE NOT EXISTS (SELECT * FROM pets WHERE id=2);
INSERT INTO pets (name, birth_date, type_id, owner_id) SELECT 'Rosy', '2001-04-17', 2, 3 WHERE NOT EXISTS (SELECT * FROM pets WHERE id=3);
INSERT INTO pets (name, birth_date, type_id, owner_id) SELECT 'Jewel', '2000-03-07', 2, 3 WHERE NOT EXISTS (SELECT * FROM pets WHERE id=4);
INSERT INTO pets (name, birth_date, type_id, owner_id) SELECT 'Iggy', '2000-11-30', 3, 4 WHERE NOT EXISTS (SELECT * FROM pets WHERE id=5);
INSERT INTO pets (name, birth_date, type_id, owner_id) SELECT 'George', '2000-01-20', 4, 5 WHERE NOT EXISTS (SELECT * FROM pets WHERE id=6);
INSERT INTO pets (name, birth_date, type_id, owner_id) SELECT 'Samantha', '1995-09-04', 1, 6 WHERE NOT EXISTS (SELECT * FROM pets WHERE id=7);
INSERT INTO pets (name, birth_date, type_id, owner_id) SELECT 'Max', '1995-09-04', 1, 6 WHERE NOT EXISTS (SELECT * FROM pets WHERE id=8);
INSERT INTO pets (name, birth_date, type_id, owner_id) SELECT 'Lucky', '1999-08-06', 5, 7 WHERE NOT EXISTS (SELECT * FROM pets WHERE id=9);
INSERT INTO pets (name, birth_date, type_id, owner_id) SELECT 'Mulligan', '1997-02-24', 2, 8 WHERE NOT EXISTS (SELECT * FROM pets WHERE id=10);
INSERT INTO pets (name, birth_date, type_id, owner_id) SELECT 'Freddy', '2000-03-09', 5, 9 WHERE NOT EXISTS (SELECT * FROM pets WHERE id=11);
INSERT INTO pets (name, birth_date, type_id, owner_id) SELECT 'Lucky', '2000-06-24', 2, 10 WHERE NOT EXISTS (SELECT * FROM pets WHERE id=12);
INSERT INTO pets (name, birth_date, type_id, owner_id) SELECT 'Sly', '2002-06-08', 1, 10 WHERE NOT EXISTS (SELECT * FROM pets WHERE id=13);

INSERT INTO visits (pet_id, visit_date, description) SELECT 7, '2010-03-04', 'rabies shot' WHERE NOT EXISTS (SELECT * FROM visits WHERE id=1);
INSERT INTO visits (pet_id, visit_date, description) SELECT 8, '2011-03-04', 'rabies shot' WHERE NOT EXISTS (SELECT * FROM visits WHERE id=2);
INSERT INTO visits (pet_id, visit_date, description) SELECT 8, '2009-06-04', 'neutered' WHERE NOT EXISTS (SELECT * FROM visits WHERE id=3);
INSERT INTO visits (pet_id, visit_date, description) SELECT 7, '2008-09-04', 'spayed' WHERE NOT EXISTS (SELECT * FROM visits WHERE id=4);

-- Partner hospitals and treatment schedules
INSERT INTO partner_hospitals (name, region, address, telephone) SELECT '베트코어 서울본원', '서울', '서울특별시 강남구 테헤란로 100', '0212345678' WHERE NOT EXISTS (SELECT * FROM partner_hospitals WHERE id=1);
INSERT INTO partner_hospitals (name, region, address, telephone) SELECT '한강동물의료센터', '서울', '서울특별시 마포구 월드컵로 20', '0223456789' WHERE NOT EXISTS (SELECT * FROM partner_hospitals WHERE id=2);
INSERT INTO partner_hospitals (name, region, address, telephone) SELECT '해운대동물병원', '부산', '부산광역시 해운대구 센텀로 30', '0513456789' WHERE NOT EXISTS (SELECT * FROM partner_hospitals WHERE id=3);
INSERT INTO partner_hospitals (name, region, address, telephone) SELECT '달구벌동물메디컬', '대구', '대구광역시 수성구 달구벌대로 40', '0534567890' WHERE NOT EXISTS (SELECT * FROM partner_hospitals WHERE id=4);
INSERT INTO partner_hospitals (name, region, address, telephone) SELECT '빛고을동물병원', '광주', '광주광역시 서구 상무대로 50', '0625678901' WHERE NOT EXISTS (SELECT * FROM partner_hospitals WHERE id=5);
INSERT INTO partner_hospitals (name, region, address, telephone) SELECT '한밭동물의료원', '대전', '대전광역시 유성구 대학로 60', '0426789012' WHERE NOT EXISTS (SELECT * FROM partner_hospitals WHERE id=6);

INSERT INTO treatment_schedules (hospital_id, treatment_name, treatment_date, start_time, capacity, reserved_count, booking_opens_at) SELECT 1, '희귀질환 치료제 투여', '2026-12-23', '10:00:00', 300, 0, '2026-11-02 10:00:00' WHERE NOT EXISTS (SELECT * FROM treatment_schedules WHERE id=1);
INSERT INTO treatment_schedules (hospital_id, treatment_name, treatment_date, start_time, capacity, reserved_count, booking_opens_at) SELECT 2, '희귀질환 치료제 투여', '2026-12-23', '10:00:00', 100, 0, '2026-11-02 10:00:00' WHERE NOT EXISTS (SELECT * FROM treatment_schedules WHERE id=2);
INSERT INTO treatment_schedules (hospital_id, treatment_name, treatment_date, start_time, capacity, reserved_count, booking_opens_at) SELECT 3, '희귀질환 치료제 투여', '2026-12-23', '10:00:00', 50, 0, '2026-11-02 10:00:00' WHERE NOT EXISTS (SELECT * FROM treatment_schedules WHERE id=3);
INSERT INTO treatment_schedules (hospital_id, treatment_name, treatment_date, start_time, capacity, reserved_count, booking_opens_at) SELECT 4, '희귀질환 치료제 투여', '2026-12-23', '10:00:00', 30, 0, '2026-11-02 10:00:00' WHERE NOT EXISTS (SELECT * FROM treatment_schedules WHERE id=4);
INSERT INTO treatment_schedules (hospital_id, treatment_name, treatment_date, start_time, capacity, reserved_count, booking_opens_at) SELECT 5, '희귀질환 치료제 투여', '2026-12-23', '10:00:00', 30, 0, '2026-11-02 10:00:00' WHERE NOT EXISTS (SELECT * FROM treatment_schedules WHERE id=5);
INSERT INTO treatment_schedules (hospital_id, treatment_name, treatment_date, start_time, capacity, reserved_count, booking_opens_at) SELECT 6, '희귀질환 치료제 투여', '2026-12-23', '10:00:00', 5, 0, '2026-11-02 10:00:00' WHERE NOT EXISTS (SELECT * FROM treatment_schedules WHERE id=6);
INSERT INTO treatment_schedules (hospital_id, treatment_name, treatment_date, start_time, capacity, reserved_count, booking_opens_at) SELECT 1, '희귀질환 치료제 투여', '2027-01-20', '10:00:00', 300, 0, '2026-12-18 10:00:00' WHERE NOT EXISTS (SELECT * FROM treatment_schedules WHERE id=7);
INSERT INTO treatment_schedules (hospital_id, treatment_name, treatment_date, start_time, capacity, reserved_count, booking_opens_at) SELECT 2, '희귀질환 치료제 투여', '2027-01-20', '10:00:00', 100, 0, '2026-12-18 10:00:00' WHERE NOT EXISTS (SELECT * FROM treatment_schedules WHERE id=8);
INSERT INTO treatment_schedules (hospital_id, treatment_name, treatment_date, start_time, capacity, reserved_count, booking_opens_at) SELECT 3, '희귀질환 치료제 투여', '2027-01-20', '10:00:00', 50, 0, '2026-12-18 10:00:00' WHERE NOT EXISTS (SELECT * FROM treatment_schedules WHERE id=9);
INSERT INTO treatment_schedules (hospital_id, treatment_name, treatment_date, start_time, capacity, reserved_count, booking_opens_at) SELECT 4, '희귀질환 치료제 투여', '2027-01-20', '10:00:00', 30, 0, '2026-12-18 10:00:00' WHERE NOT EXISTS (SELECT * FROM treatment_schedules WHERE id=10);
INSERT INTO treatment_schedules (hospital_id, treatment_name, treatment_date, start_time, capacity, reserved_count, booking_opens_at) SELECT 5, '희귀질환 치료제 투여', '2027-01-20', '10:00:00', 30, 0, '2026-12-18 10:00:00' WHERE NOT EXISTS (SELECT * FROM treatment_schedules WHERE id=11);
INSERT INTO treatment_schedules (hospital_id, treatment_name, treatment_date, start_time, capacity, reserved_count, booking_opens_at) SELECT 6, '희귀질환 치료제 투여', '2027-01-20', '10:00:00', 5, 0, '2026-12-18 10:00:00' WHERE NOT EXISTS (SELECT * FROM treatment_schedules WHERE id=12);
