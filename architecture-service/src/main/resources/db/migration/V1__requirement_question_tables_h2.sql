CREATE TABLE IF NOT EXISTS requirement_questions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    question_text TEXT,
    question_type VARCHAR(50),
    options TEXT,
    display_order INT,
    category VARCHAR(100)
);

INSERT INTO requirement_questions (question_text, question_type, options, display_order, category) VALUES
-- Scale and Performance
('What is the expected number of concurrent users for your application?', 'RANGE', '0-100,101-1000,1001-10000,10000+', 1, 'SCALE'),
('What is your expected data storage needs in the first year?', 'RANGE', '0-10GB,10-100GB,100GB-1TB,1TB+', 2, 'SCALE'),
('Do you need real-time data processing?', 'BOOLEAN', 'Yes,No', 3, 'PERFORMANCE'),

-- Availability and Reliability
('What is your required system uptime percentage?', 'SINGLE_CHOICE', '99.9%,99.99%,99.999%', 4, 'RELIABILITY'),
('Is your application mission-critical requiring high availability?', 'BOOLEAN', 'Yes,No', 5, 'RELIABILITY'),
('Do you need disaster recovery capabilities?', 'BOOLEAN', 'Yes,No', 6, 'RELIABILITY'),

-- Security
('Will your application handle sensitive user data?', 'BOOLEAN', 'Yes,No', 7, 'SECURITY'),
('Do you need to comply with specific security regulations (e.g., GDPR, HIPAA)?', 'MULTIPLE_CHOICE', 'GDPR,HIPAA,PCI-DSS,SOC2,None', 8, 'SECURITY'),
('Do you require user authentication?', 'SINGLE_CHOICE', 'No Authentication,Simple Login,SSO,Multi-factor Authentication', 9, 'SECURITY'),

-- Integration
('Will your application need to integrate with external services?', 'MULTIPLE_CHOICE', 'Payment Systems,Email Services,Social Media,Third-party APIs,None', 10, 'INTEGRATION'),
('Do you need offline functionality?', 'BOOLEAN', 'Yes,No', 11, 'INTEGRATION'),

-- User Interface
('What type of user interface does your application require?', 'MULTIPLE_CHOICE', 'Web Application,Mobile App,Desktop Application,CLI', 12, 'UI'),
('Do you need to support multiple languages/localization?', 'BOOLEAN', 'Yes,No', 13, 'UI'),

-- Data Management
('What type of data will your application primarily handle?', 'MULTIPLE_CHOICE', 'Structured Data,Unstructured Data,Files/Documents,Media Content', 14, 'DATA'),
('Do you need real-time data synchronization?', 'BOOLEAN', 'Yes,No', 15, 'DATA'),

-- Deployment
('What is your preferred deployment environment?', 'SINGLE_CHOICE', 'Cloud,On-Premise,Hybrid', 16, 'DEPLOYMENT'),
('Do you require automated scaling capabilities?', 'BOOLEAN', 'Yes,No', 17, 'DEPLOYMENT'),

-- Maintenance
('Do you need to support multiple versions of the application simultaneously?', 'BOOLEAN', 'Yes,No', 18, 'MAINTENANCE'),
('What is your expected release frequency?', 'SINGLE_CHOICE', 'Daily,Weekly,Monthly,Quarterly', 19, 'MAINTENANCE'),

-- Cost
('What is the importance of initial development speed vs long-term maintainability?', 'SINGLE_CHOICE', 'Rapid Development,Balanced,Long-term Maintainability', 20, 'COST'),
('Do you need to optimize for specific cost constraints?', 'BOOLEAN', 'Yes,No', 21, 'COST');

CREATE INDEX idx_question_category ON requirement_questions(category);
CREATE INDEX idx_question_display_order ON requirement_questions(display_order);
