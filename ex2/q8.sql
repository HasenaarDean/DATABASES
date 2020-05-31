CREATE VIEW uniquePatients AS SELECT Doctor.did, Patient.pid, bmi FROM 
Doctor NATURAL JOIN Patient NATURAL JOIN Visit GROUP BY Doctor.did,  Patient.pid;

CREATE VIEW bmiAverages AS SELECT did, AVG(bmi) AS avg_bmi FROM 
uniquePatients GROUP BY did; 

SELECT distinct did FROM bmiAverages GROUP BY did, avg_bmi 
HAVING avg_bmi >= ALL(SELECT avg_bmi FROM bmiAverages) ORDER BY did ASC;
