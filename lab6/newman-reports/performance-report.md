# Manual API Performance Report

**Environment**: Tomcat + Java Servlets  
**Test Date**: 13.12.2025, 18:57:59  
**Iterations**: 10  

## Response Time Statistics (ms)

| API Endpoint | Min | Max | Average | Median | Std Dev |
|-------------|-----|-----|---------|--------|----------|

### Users

| Endpoint | Min | Max | Avg | Median | Std Dev |
|----------|-----|-----|-----|--------|----------|
| POST Create User | 2 | 4 | 3 | 3 | 1 |
| GET All Users | 2 | 4 | 3 | 3 | 1 |
| GET User by ID | 2 | 4 | 3 | 3 | 1 |
| PUT Update User | 3 | 4 | 3 | 3 | 0 |
| DELETE User | 2 | 4 | 3 | 3 | 1 |
| GET Functions by User ID | 2 | 4 | 3 | 3 | 1 |

### Functions

| Endpoint | Min | Max | Avg | Median | Std Dev |
|----------|-----|-----|-----|--------|----------|
| POST Create Function | 2 | 4 | 3 | 3 | 0 |
| GET All Functions | 3 | 4 | 3 | 3 | 0 |
| GET Function by ID | 3 | 4 | 3 | 3 | 0 |

### Other

| Endpoint | Min | Max | Avg | Median | Std Dev |
|----------|-----|-----|-----|--------|----------|
| Health Check | 3 | 31 | 6 | 3 | 8 |

## Summary

| Metric | Value |
|--------|-------|
| Total Requests Tested | 10 |
| Average Response Time | 3 ms |
| Fastest Endpoint | POST Create User (3 ms) |
| Slowest Endpoint | Health Check (6 ms) |
