# Garage Service Validation Report

- Date: 2025-11-30 18:18:27
- BaseUrl: http://localhost:8082
- Health Status: 200
- Garage ID: 450924b6-a9d6-463b-97bf-2d18d42a21e7
- Vehicles Requested: 3
- Vehicles Created: 3
- Kafka Consumer Logs Found: True
- Capacity Max Attempt: 120
- Capacity Enforced: False
- Capacity Enforced At Attempt: 

## Next Steps
- Inspect Kafka UI at http://localhost:8090 for topic vehicule.created.
- Verify app logs for consumer acknowledgments.
- Adjust MaxAttempt if capacity is higher than expected.
