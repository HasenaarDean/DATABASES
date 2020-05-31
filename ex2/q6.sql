SELECT distinct did FROM Doctor NATURAL JOIN Patient NATURAL JOIN Visit 
WHERE bmi > 30 GROUP BY did HAVING count(*) = 3 ORDER BY did ASC;
