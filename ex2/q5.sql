SELECT distinct Doctor.dname FROM Doctor WHERE specialty = 'pediatrician' 
and NOT EXISTS (SELECT Patient.pid From Patient WHERE gender = 'M' and bmi > 
30 EXCEPT SELECT pid FROM Visit NATURAL JOIN Patient WHERE did = Doctor.did) 
ORDER BY dname ASC;
