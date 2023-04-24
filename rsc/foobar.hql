SELECT
    partition_date_hour_utc,
    COUNT(*) AS record_count
FROM
    prod.core_quantum_events
WHERE
    partition_date_hour_utc = '2023-04-20_10'
