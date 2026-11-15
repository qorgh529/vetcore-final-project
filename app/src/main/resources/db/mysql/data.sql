INSERT IGNORE INTO vets VALUES (1, 'James', 'Carter');
INSERT IGNORE INTO vets VALUES (2, 'Helen', 'Leary');
INSERT IGNORE INTO vets VALUES (3, 'Linda', 'Douglas');
INSERT IGNORE INTO vets VALUES (4, 'Rafael', 'Ortega');
INSERT IGNORE INTO vets VALUES (5, 'Henry', 'Stevens');
INSERT IGNORE INTO vets VALUES (6, 'Sharon', 'Jenkins');

INSERT IGNORE INTO specialties VALUES (1, 'radiology');
INSERT IGNORE INTO specialties VALUES (2, 'surgery');
INSERT IGNORE INTO specialties VALUES (3, 'dentistry');

INSERT IGNORE INTO vet_specialties VALUES (2, 1);
INSERT IGNORE INTO vet_specialties VALUES (3, 2);
INSERT IGNORE INTO vet_specialties VALUES (3, 3);
INSERT IGNORE INTO vet_specialties VALUES (4, 2);
INSERT IGNORE INTO vet_specialties VALUES (5, 1);

INSERT IGNORE INTO types VALUES (1, 'cat');
INSERT IGNORE INTO types VALUES (2, 'dog');
INSERT IGNORE INTO types VALUES (3, 'lizard');
INSERT IGNORE INTO types VALUES (4, 'snake');
INSERT IGNORE INTO types VALUES (5, 'bird');
INSERT IGNORE INTO types VALUES (6, 'hamster');

INSERT IGNORE INTO owners VALUES (1, 'George', 'Franklin', '110 W. Liberty St.', 'Madison', '6085551023');
INSERT IGNORE INTO owners VALUES (2, 'Betty', 'Davis', '638 Cardinal Ave.', 'Sun Prairie', '6085551749');
INSERT IGNORE INTO owners VALUES (3, 'Eduardo', 'Rodriquez', '2693 Commerce St.', 'McFarland', '6085558763');
INSERT IGNORE INTO owners VALUES (4, 'Harold', 'Davis', '563 Friendly St.', 'Windsor', '6085553198');
INSERT IGNORE INTO owners VALUES (5, 'Peter', 'McTavish', '2387 S. Fair Way', 'Madison', '6085552765');
INSERT IGNORE INTO owners VALUES (6, 'Jean', 'Coleman', '105 N. Lake St.', 'Monona', '6085552654');
INSERT IGNORE INTO owners VALUES (7, 'Jeff', 'Black', '1450 Oak Blvd.', 'Monona', '6085555387');
INSERT IGNORE INTO owners VALUES (8, 'Maria', 'Escobito', '345 Maple St.', 'Madison', '6085557683');
INSERT IGNORE INTO owners VALUES (9, 'David', 'Schroeder', '2749 Blackhawk Trail', 'Madison', '6085559435');
INSERT IGNORE INTO owners VALUES (10, 'Carlos', 'Estaban', '2335 Independence La.', 'Waunakee', '6085555487');

INSERT IGNORE INTO pets VALUES (1, 'Leo', '2000-09-07', 1, 1);
INSERT IGNORE INTO pets VALUES (2, 'Basil', '2002-08-06', 6, 2);
INSERT IGNORE INTO pets VALUES (3, 'Rosy', '2001-04-17', 2, 3);
INSERT IGNORE INTO pets VALUES (4, 'Jewel', '2000-03-07', 2, 3);
INSERT IGNORE INTO pets VALUES (5, 'Iggy', '2000-11-30', 3, 4);
INSERT IGNORE INTO pets VALUES (6, 'George', '2000-01-20', 4, 5);
INSERT IGNORE INTO pets VALUES (7, 'Samantha', '1995-09-04', 1, 6);
INSERT IGNORE INTO pets VALUES (8, 'Max', '1995-09-04', 1, 6);
INSERT IGNORE INTO pets VALUES (9, 'Lucky', '1999-08-06', 5, 7);
INSERT IGNORE INTO pets VALUES (10, 'Mulligan', '1997-02-24', 2, 8);
INSERT IGNORE INTO pets VALUES (11, 'Freddy', '2000-03-09', 5, 9);
INSERT IGNORE INTO pets VALUES (12, 'Lucky', '2000-06-24', 2, 10);
INSERT IGNORE INTO pets VALUES (13, 'Sly', '2002-06-08', 1, 10);

INSERT IGNORE INTO visits VALUES (1, 7, '2010-03-04', 'rabies shot');
INSERT IGNORE INTO visits VALUES (2, 8, '2011-03-04', 'rabies shot');
INSERT IGNORE INTO visits VALUES (3, 8, '2009-06-04', 'neutered');
INSERT IGNORE INTO visits VALUES (4, 7, '2008-09-04', 'spayed');

-- Partner hospitals and treatment schedules
INSERT IGNORE INTO partner_hospitals (id, name, region, address, telephone) VALUES (1, '베트코어 서울본원', '서울', '서울특별시 강남구 테헤란로 100', '0212345678');
INSERT IGNORE INTO partner_hospitals (id, name, region, address, telephone) VALUES (2, '한강동물의료센터', '서울', '서울특별시 마포구 월드컵로 20', '0223456789');
INSERT IGNORE INTO partner_hospitals (id, name, region, address, telephone) VALUES (3, '해운대동물병원', '부산', '부산광역시 해운대구 센텀로 30', '0513456789');
INSERT IGNORE INTO partner_hospitals (id, name, region, address, telephone) VALUES (4, '달구벌동물메디컬', '대구', '대구광역시 수성구 달구벌대로 40', '0534567890');
INSERT IGNORE INTO partner_hospitals (id, name, region, address, telephone) VALUES (5, '빛고을동물병원', '광주', '광주광역시 서구 상무대로 50', '0625678901');
INSERT IGNORE INTO partner_hospitals (id, name, region, address, telephone) VALUES (6, '한밭동물의료원', '대전', '대전광역시 유성구 대학로 60', '0426789012');

INSERT IGNORE INTO treatment_schedules (id, hospital_id, treatment_name, treatment_date, start_time, capacity, reserved_count, booking_opens_at) VALUES (1, 1, '희귀질환 치료제 투여', '2026-12-23', '10:00:00', 300, 0, '2026-11-02 10:00:00');
INSERT IGNORE INTO treatment_schedules (id, hospital_id, treatment_name, treatment_date, start_time, capacity, reserved_count, booking_opens_at) VALUES (2, 2, '희귀질환 치료제 투여', '2026-12-23', '10:00:00', 100, 0, '2026-11-02 10:00:00');
INSERT IGNORE INTO treatment_schedules (id, hospital_id, treatment_name, treatment_date, start_time, capacity, reserved_count, booking_opens_at) VALUES (3, 3, '희귀질환 치료제 투여', '2026-12-23', '10:00:00', 50, 0, '2026-11-02 10:00:00');
INSERT IGNORE INTO treatment_schedules (id, hospital_id, treatment_name, treatment_date, start_time, capacity, reserved_count, booking_opens_at) VALUES (4, 4, '희귀질환 치료제 투여', '2026-12-23', '10:00:00', 30, 0, '2026-11-02 10:00:00');
INSERT IGNORE INTO treatment_schedules (id, hospital_id, treatment_name, treatment_date, start_time, capacity, reserved_count, booking_opens_at) VALUES (5, 5, '희귀질환 치료제 투여', '2026-12-23', '10:00:00', 30, 0, '2026-11-02 10:00:00');
INSERT IGNORE INTO treatment_schedules (id, hospital_id, treatment_name, treatment_date, start_time, capacity, reserved_count, booking_opens_at) VALUES (6, 6, '희귀질환 치료제 투여', '2026-12-23', '10:00:00', 5, 0, '2026-11-02 10:00:00');
INSERT IGNORE INTO treatment_schedules (id, hospital_id, treatment_name, treatment_date, start_time, capacity, reserved_count, booking_opens_at) VALUES (7, 1, '희귀질환 치료제 투여', '2027-01-20', '10:00:00', 300, 0, '2026-12-18 10:00:00');
INSERT IGNORE INTO treatment_schedules (id, hospital_id, treatment_name, treatment_date, start_time, capacity, reserved_count, booking_opens_at) VALUES (8, 2, '희귀질환 치료제 투여', '2027-01-20', '10:00:00', 100, 0, '2026-12-18 10:00:00');
INSERT IGNORE INTO treatment_schedules (id, hospital_id, treatment_name, treatment_date, start_time, capacity, reserved_count, booking_opens_at) VALUES (9, 3, '희귀질환 치료제 투여', '2027-01-20', '10:00:00', 50, 0, '2026-12-18 10:00:00');
INSERT IGNORE INTO treatment_schedules (id, hospital_id, treatment_name, treatment_date, start_time, capacity, reserved_count, booking_opens_at) VALUES (10, 4, '희귀질환 치료제 투여', '2027-01-20', '10:00:00', 30, 0, '2026-12-18 10:00:00');
INSERT IGNORE INTO treatment_schedules (id, hospital_id, treatment_name, treatment_date, start_time, capacity, reserved_count, booking_opens_at) VALUES (11, 5, '희귀질환 치료제 투여', '2027-01-20', '10:00:00', 30, 0, '2026-12-18 10:00:00');
INSERT IGNORE INTO treatment_schedules (id, hospital_id, treatment_name, treatment_date, start_time, capacity, reserved_count, booking_opens_at) VALUES (12, 6, '희귀질환 치료제 투여', '2027-01-20', '10:00:00', 5, 0, '2026-12-18 10:00:00');
