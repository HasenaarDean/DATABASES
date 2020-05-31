SELECT distinct pid, pname FROM Doctor NATURAL JOIN Patient NATURAL JOIN 
Visit WHERE specialty = 'orthopedist' INTERSECT SELECT pid, pname FROM 
Doctor NATURAL JOIN Patient NATURAL JOIN Visit WHERE specialty = 
'pediatrician' ORDER BY pid ASC, pname ASC;
