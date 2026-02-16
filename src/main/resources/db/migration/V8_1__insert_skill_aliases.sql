-- Languages
INSERT INTO SKILL_ALIASES (SKILL_ID, ALIAS) VALUES
    (1, 'java'),
    (1, 'jvm'),
    (2, 'python'),
    (3, 'c#'),
    (3, 'csharp'),
    (4, 'c++'),
    (4, 'cpp'),
    (5, 'c'),
    (6, 'javascript'),
    (6, 'js'),
    (7, 'typescript'),
    (7, 'ts'),
    (8, 'go'),
    (8, 'golang'),
    (9, 'rust'),
    (10, 'kotlin'),
    (11, 'swift'),
    (12, 'ruby'),
    (13, 'php'),
    (14, 'scala'),
    (15, 'r'),
    (16, 'dart'),
    (17, 'objective-c'),
    (17, 'objc'),
    (18, 'shell scripting'),
    (19, 'bash'),
    (20, 'powershell');
-- Web fundamentals
INSERT INTO SKILL_ALIASES (SKILL_ID, ALIAS) VALUES
    (21, 'html'),
    (22, 'css'),
    (23, 'sass'),
    (24, 'less');
-- Frontend frameworks
INSERT INTO SKILL_ALIASES (SKILL_ID, ALIAS) VALUES
    (25, 'react'),
    (25, 'reactjs'),
    (26, 'angular'),
    (26, 'angularjs'),
    (27, 'vue'),
    (27, 'vuejs'),
    (28, 'svelte'),
    (29, 'nextjs'),
    (30, 'nuxtjs'),
    (31, 'redux'),
    (32, 'rxjs'),
    (33, 'webpack'),
    (34, 'vite'),
    (35, 'tailwind'),
    (35, 'tailwindcss'),
    (36, 'bootstrap'),
    (37, 'mui'),
    (37, 'material ui');
-- Backend
INSERT INTO SKILL_ALIASES (SKILL_ID, ALIAS) VALUES
    (38, 'spring'),
    (39, 'spring boot'),
    (40, '.net'),
    (40, 'dotnet'),
    (41, 'asp.net'),
    (41, 'aspnet'),
    (42, 'node'),
    (42, 'nodejs'),
    (43, 'express'),
    (44, 'nestjs'),
    (45, 'django'),
    (46, 'flask'),
    (47, 'fastapi'),
    (48, 'rails'),
    (49, 'laravel'),
    (50, 'symfony'),
    (51, 'gin');
-- Mobile
INSERT INTO SKILL_ALIASES (SKILL_ID, ALIAS) VALUES
    (52, 'android'),
    (53, 'ios'),
    (54, 'react native'),
    (55, 'flutter'),
    (56, 'xamarin');
-- Databases
INSERT INTO SKILL_ALIASES (SKILL_ID, ALIAS) VALUES
    (57, 'postgres'),
    (57, 'postgresql'),
    (58, 'mysql'),
    (59, 'mariadb'),
    (60, 'sql server'),
    (61, 'oracle'),
    (62, 'sqlite'),
    (63, 'mongodb'),
    (64, 'redis'),
    (65, 'elasticsearch'),
    (66, 'cassandra'),
    (67, 'dynamodb');
-- Data & streaming
INSERT INTO SKILL_ALIASES (SKILL_ID, ALIAS) VALUES
    (68, 'kafka'),
    (69, 'rabbitmq'),
    (70, 'activemq'),
    (71, 'spark'),
    (72, 'hadoop'),
    (73, 'hive'),
    (74, 'airflow'),
    (75, 'snowflake'),
    (76, 'bigquery'),
    (77, 'databricks');
-- Cloud & DevOps
INSERT INTO SKILL_ALIASES (SKILL_ID, ALIAS) VALUES
    (78, 'aws'),
    (79, 'azure'),
    (80, 'gcp'),
    (80, 'google cloud'),
    (81, 'docker'),
    (82, 'k8s'),
    (82, 'kubernetes'),
    (83, 'terraform'),
    (84, 'ansible'),
    (85, 'pulumi'),
    (86, 'helm'),
    (87, 'jenkins'),
    (88, 'gitlab ci'),
    (89, 'github actions'),
    (90, 'circleci');
-- Build & package managers
INSERT INTO SKILL_ALIASES (SKILL_ID, ALIAS) VALUES
    (91, 'maven'),
    (92, 'gradle'),
    (93, 'ant'),
    (94, 'npm'),
    (95, 'yarn'),
    (96, 'pnpm');
-- Testing & QA
INSERT INTO SKILL_ALIASES (SKILL_ID, ALIAS) VALUES
    (97, 'junit'),
    (98, 'testng'),
    (99, 'mockito'),
    (100, 'selenium'),
    (101, 'cypress'),
    (102, 'playwright'),
    (103, 'jest'),
    (104, 'mocha'),
    (105, 'chai'),
    (106, 'pytest'),
    (107, 'rspec');
-- APIs & protocols
INSERT INTO SKILL_ALIASES (SKILL_ID, ALIAS) VALUES
    (108, 'rest'),
    (109, 'graphql'),
    (110, 'grpc'),
    (111, 'websockets');
-- Security & auth
INSERT INTO SKILL_ALIASES (SKILL_ID, ALIAS) VALUES
    (112, 'oauth'),
    (112, 'oauth2'),
    (113, 'oidc'),
    (114, 'jwt'),
    (115, 'saml'),
    (116, 'ssl'),
    (116, 'tls');
-- Observability & monitoring
INSERT INTO SKILL_ALIASES (SKILL_ID, ALIAS) VALUES
    (117, 'prometheus'),
    (118, 'grafana'),
    (119, 'elk'),
    (119, 'elastic stack'),
    (120, 'opentelemetry'),
    (121, 'datadog'),
    (122, 'new relic');
-- Config & messaging
INSERT INTO SKILL_ALIASES (SKILL_ID, ALIAS) VALUES
    (123, 'nginx'),
    (124, 'apache'),
    (124, 'httpd'),
    (125, 'haproxy'),
    (126, 'consul'),
    (127, 'etcd');
-- UI/UX
INSERT INTO SKILL_ALIASES (SKILL_ID, ALIAS) VALUES
    (128, 'figma'),
    (129, 'adobe xd'),
    (129, 'xd');
-- Other technical
INSERT INTO SKILL_ALIASES (SKILL_ID, ALIAS) VALUES
    (130, 'regex'),
    (131, 'uml'),
    (132, 'linux'),
    (133, 'windows'),
    (134, 'macos'),
    (135, 'git'),
    (136, 'svn');
-- Soft skills
INSERT INTO SKILL_ALIASES (SKILL_ID, ALIAS) VALUES
    (137, 'communication'),
    (137, 'comms'),
    (138, 'teamwork'),
    (139, 'leadership'),
    (140, 'problem solving'),
    (141, 'critical thinking'),
    (142, 'time management'),
    (143, 'adaptability'),
    (144, 'creativity'),
    (145, 'empathy'),
    (146, 'negotiation'),
    (147, 'conflict resolution'),
    (148, 'mentoring'),
    (149, 'presentation'),
    (150, 'stakeholder management'),
    (151, 'product sense'),
    (152, 'attention to detail'),
    (153, 'accountability'),
    (154, 'decision making'),
    (155, 'collaboration'),
    (156, 'ownership');