SELECT distinct Patient.pid, Doctor.did FROM Patient, Doctor 
WHERE NOT EXISTS (SELECT * FROM Visit WHERE Patient.pid = Visit.pid 
and Doctor.did = Visit.did) ORDER BY pid ASC, did ASC;
