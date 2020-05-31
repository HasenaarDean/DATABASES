UPDATE Doctor 
SET clinic = (CASE  
                 WHEN clinic = 1 THEN 2
                 WHEN clinic = 2 THEN 1
                 ELSE clinic
END) 
WHERE clinic IN (1, 2);
