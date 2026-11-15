INSERT INTO vets VALUES (default, 'James', 'Carter');
INSERT INTO vets VALUES (default, 'Helen', 'Leary');
INSERT INTO vets VALUES (default, 'Linda', 'Douglas');
INSERT INTO vets VALUES (default, 'Rafael', 'Ortega');
INSERT INTO vets VALUES (default, 'Henry', 'Stevens');
INSERT INTO vets VALUES (default, 'Sharon', 'Jenkins');

INSERT INTO specialties VALUES (default, 'radiology');
INSERT INTO specialties VALUES (default, 'surgery');
INSERT INTO specialties VALUES (default, 'dentistry');

INSERT INTO vet_specialties VALUES (2, 1);
INSERT INTO vet_specialties VALUES (3, 2);
INSERT INTO vet_specialties VALUES (3, 3);
INSERT INTO vet_specialties VALUES (4, 2);
INSERT INTO vet_specialties VALUES (5, 1);

INSERT INTO types VALUES (default, 'cat');
INSERT INTO types VALUES (default, 'dog');
INSERT INTO types VALUES (default, 'lizard');
INSERT INTO types VALUES (default, 'snake');
INSERT INTO types VALUES (default, 'bird');
INSERT INTO types VALUES (default, 'hamster');

INSERT INTO owners VALUES (default, 'George', 'Franklin', '110 W. Liberty St.', 'Madison', '6085551023');
INSERT INTO owners VALUES (default, 'Betty', 'Davis', '638 Cardinal Ave.', 'Sun Prairie', '6085551749');
INSERT INTO owners VALUES (default, 'Eduardo', 'Rodriquez', '2693 Commerce St.', 'McFarland', '6085558763');
INSERT INTO owners VALUES (default, 'Harold', 'Davis', '563 Friendly St.', 'Windsor', '6085553198');
INSERT INTO owners VALUES (default, 'Peter', 'McTavish', '2387 S. Fair Way', 'Madison', '6085552765');
INSERT INTO owners VALUES (default, 'Jean', 'Coleman', '105 N. Lake St.', 'Monona', '6085552654');
INSERT INTO owners VALUES (default, 'Jeff', 'Black', '1450 Oak Blvd.', 'Monona', '6085555387');
INSERT INTO owners VALUES (default, 'Maria', 'Escobito', '345 Maple St.', 'Madison', '6085557683');
INSERT INTO owners VALUES (default, 'David', 'Schroeder', '2749 Blackhawk Trail', 'Madison', '6085559435');
INSERT INTO owners VALUES (default, 'Carlos', 'Estaban', '2335 Independence La.', 'Waunakee', '6085555487');

INSERT INTO pets VALUES (default, 'Leo', '2010-09-07', 1, 1);
INSERT INTO pets VALUES (default, 'Basil', '2012-08-06', 6, 2);
INSERT INTO pets VALUES (default, 'Rosy', '2011-04-17', 2, 3);
INSERT INTO pets VALUES (default, 'Jewel', '2010-03-07', 2, 3);
INSERT INTO pets VALUES (default, 'Iggy', '2010-11-30', 3, 4);
INSERT INTO pets VALUES (default, 'George', '2010-01-20', 4, 5);
INSERT INTO pets VALUES (default, 'Samantha', '2012-09-04', 1, 6);
INSERT INTO pets VALUES (default, 'Max', '2012-09-04', 1, 6);
INSERT INTO pets VALUES (default, 'Lucky', '2011-08-06', 5, 7);
INSERT INTO pets VALUES (default, 'Mulligan', '2007-02-24', 2, 8);
INSERT INTO pets VALUES (default, 'Freddy', '2010-03-09', 5, 9);
INSERT INTO pets VALUES (default, 'Lucky', '2010-06-24', 2, 10);
INSERT INTO pets VALUES (default, 'Sly', '2012-06-08', 1, 10);

INSERT INTO visits VALUES (default, 7, '2013-01-01', 'rabies shot');
INSERT INTO visits VALUES (default, 8, '2013-01-02', 'rabies shot');
INSERT INTO visits VALUES (default, 8, '2013-01-03', 'neutered');
INSERT INTO visits VALUES (default, 7, '2013-01-04', 'spayed');

-- Partner hospitals and treatment schedules
INSERT INTO partner_hospitals (name, region, address, telephone) VALUES ('베트코어 서울본원', '서울', '서울특별시 강남구 테헤란로 100', '0212345678');
INSERT INTO partner_hospitals (name, region, address, telephone) VALUES ('한강동물의료센터', '서울', '서울특별시 마포구 월드컵로 20', '0223456789');
INSERT INTO partner_hospitals (name, region, address, telephone) VALUES ('해운대동물병원', '부산', '부산광역시 해운대구 센텀로 30', '0513456789');
INSERT INTO partner_hospitals (name, region, address, telephone) VALUES ('달구벌동물메디컬', '대구', '대구광역시 수성구 달구벌대로 40', '0534567890');
INSERT INTO partner_hospitals (name, region, address, telephone) VALUES ('빛고을동물병원', '광주', '광주광역시 서구 상무대로 50', '0625678901');
INSERT INTO partner_hospitals (name, region, address, telephone) VALUES ('한밭동물의료원', '대전', '대전광역시 유성구 대학로 60', '0426789012');

INSERT INTO treatment_schedules (hospital_id, treatment_name, treatment_date, start_time, capacity, reserved_count, booking_opens_at) VALUES (1, '희귀질환 치료제 투여', '2026-12-23', '10:00:00', 300, 0, '2026-11-02 10:00:00');
INSERT INTO treatment_schedules (hospital_id, treatment_name, treatment_date, start_time, capacity, reserved_count, booking_opens_at) VALUES (2, '희귀질환 치료제 투여', '2026-12-23', '10:00:00', 100, 0, '2026-11-02 10:00:00');
INSERT INTO treatment_schedules (hospital_id, treatment_name, treatment_date, start_time, capacity, reserved_count, booking_opens_at) VALUES (3, '희귀질환 치료제 투여', '2026-12-23', '10:00:00', 50, 0, '2026-11-02 10:00:00');
INSERT INTO treatment_schedules (hospital_id, treatment_name, treatment_date, start_time, capacity, reserved_count, booking_opens_at) VALUES (4, '희귀질환 치료제 투여', '2026-12-23', '10:00:00', 30, 0, '2026-11-02 10:00:00');
INSERT INTO treatment_schedules (hospital_id, treatment_name, treatment_date, start_time, capacity, reserved_count, booking_opens_at) VALUES (5, '희귀질환 치료제 투여', '2026-12-23', '10:00:00', 30, 0, '2026-11-02 10:00:00');
INSERT INTO treatment_schedules (hospital_id, treatment_name, treatment_date, start_time, capacity, reserved_count, booking_opens_at) VALUES (6, '희귀질환 치료제 투여', '2026-12-23', '10:00:00', 5, 0, '2026-11-02 10:00:00');
INSERT INTO treatment_schedules (hospital_id, treatment_name, treatment_date, start_time, capacity, reserved_count, booking_opens_at) VALUES (1, '희귀질환 치료제 투여', '2027-01-20', '10:00:00', 300, 0, '2026-12-18 10:00:00');
INSERT INTO treatment_schedules (hospital_id, treatment_name, treatment_date, start_time, capacity, reserved_count, booking_opens_at) VALUES (2, '희귀질환 치료제 투여', '2027-01-20', '10:00:00', 100, 0, '2026-12-18 10:00:00');
INSERT INTO treatment_schedules (hospital_id, treatment_name, treatment_date, start_time, capacity, reserved_count, booking_opens_at) VALUES (3, '희귀질환 치료제 투여', '2027-01-20', '10:00:00', 50, 0, '2026-12-18 10:00:00');
INSERT INTO treatment_schedules (hospital_id, treatment_name, treatment_date, start_time, capacity, reserved_count, booking_opens_at) VALUES (4, '희귀질환 치료제 투여', '2027-01-20', '10:00:00', 30, 0, '2026-12-18 10:00:00');
INSERT INTO treatment_schedules (hospital_id, treatment_name, treatment_date, start_time, capacity, reserved_count, booking_opens_at) VALUES (5, '희귀질환 치료제 투여', '2027-01-20', '10:00:00', 30, 0, '2026-12-18 10:00:00');
INSERT INTO treatment_schedules (hospital_id, treatment_name, treatment_date, start_time, capacity, reserved_count, booking_opens_at) VALUES (6, '희귀질환 치료제 투여', '2027-01-20', '10:00:00', 5, 0, '2026-12-18 10:00:00');
