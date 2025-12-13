# Manual API Performance Report

**Environment**: Tomcat + Java Servlets  
**Test Date**: 13.12.2025, 18:58:40  
**Iterations**: 10  

## Response Time Statistics (ms)

| API Endpoint | Min | Max | Average | Median | Std Dev |
|-------------|-----|-----|---------|--------|----------|

### Users

| Endpoint | Min | Max | Avg | Median | Std Dev |
|----------|-----|-----|-----|--------|----------|
| POST Create User | 2 | 5 | 3 | 3 | 1 |
| GET All Users | 2 | 4 | 3 | 3 | 1 |
| GET User by ID | 2 | 5 | 3 | 3 | 1 |
| PUT Update User | 3 | 7 | 3 | 3 | 1 |
| DELETE User | 2 | 3 | 3 | 3 | 0 |
| GET Functions by User ID | 3 | 4 | 3 | 3 | 0 |

### Functions

| Endpoint | Min | Max | Avg | Median | Std Dev |
|----------|-----|-----|-----|--------|----------|
| POST Create Function | 2 | 4 | 3 | 3 | 1 |
| GET All Functions | 2 | 3 | 3 | 3 | 0 |
| GET Function by ID | 2 | 4 | 3 | 3 | 1 |

### Other

| Endpoint | Min | Max | Avg | Median | Std Dev |
|----------|-----|-----|-----|--------|----------|
| Health Check | 2 | 22 | 5 | 3 | 6 |

## Summary

| Metric | Value |
|--------|-------|
| Total Requests Tested | 10 |
| Average Response Time | 3 ms |
| Fastest Endpoint | POST Create User (3 ms) |
| Slowest Endpoint | Health Check (5 ms) |
