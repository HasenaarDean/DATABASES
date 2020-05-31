SELECT distinct pname FROM Doctor NATURAL JOIN Patient NATURAL JOIN Visit 
WHERE fee = 0 and dname = 'Avi Cohen' ORDER BY pname ASC;
