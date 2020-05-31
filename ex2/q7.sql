SELECT distinct Doctor.did, max(fee), min(fee), avg(fee) FROM Doctor NATURAL LEFT OUTER JOIN Visit 
GROUP BY Doctor.did ORDER BY did ASC, max(fee) ASC, min(fee) ASC, avg(fee) ASC;
