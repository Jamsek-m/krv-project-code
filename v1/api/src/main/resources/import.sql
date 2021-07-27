INSERT INTO clients(id, timestamp, client_id, name, redirect_uris, secret, status, require_consent, type) VALUES('d624096c-0a58-41e1-8ab0-8a16149e8771', NOW(), 'testni', 'Testni', 'http://localhost:4200;http://localhost:8081', '09a2bdc2-4a44-461a-a690-14422cd17308', 'ENABLED', true, 'PUBLIC');
INSERT INTO clients(id, timestamp, client_id, name, redirect_uris, secret, status, require_consent, type) VALUES('65601a6c-f249-4058-a4d2-b0304f1c7374', NOW(), 'defaultni', 'Defaultni', 'http://localhost:4200;http://localhost:8081', '09a2bdc2-4a44-461a-a690-14422cd17308', 'ENABLED', false, 'PUBLIC');
INSERT INTO clients(id, timestamp, client_id, name, redirect_uris, require_consent, secret, status, type) VALUES ('13467961-3d7f-43bf-9cbd-19c7199977de', NOW(), 'admin-cli', 'Admin CLI', 'http://localhost:8080/auth', false, '7d9034a6-2e62-4468-92cb-b5e56b95edf1', 'ENABLED', 'PUBLIC');

INSERT INTO client_scopes(id, timestamp, name, client_id) VALUES ('b5066d70-e521-4cdb-85b8-da8a98aecbeb', NOW(), 'openid', 'd624096c-0a58-41e1-8ab0-8a16149e8771');
INSERT INTO client_scopes(id, timestamp, name, client_id) VALUES ('8f31bfff-ab29-4b53-aa84-0850f16b3a83', NOW(), 'email', 'd624096c-0a58-41e1-8ab0-8a16149e8771');

INSERT INTO client_scopes(id, timestamp, name, client_id) VALUES ('2d1d65e7-ba78-4d9c-b536-7aa13b255a56', NOW(), 'openid', '65601a6c-f249-4058-a4d2-b0304f1c7374');
INSERT INTO client_scopes(id, timestamp, name, client_id) VALUES ('3e7d856e-82cb-474e-a59f-7d7ffdb450e8', NOW(), 'email', '65601a6c-f249-4058-a4d2-b0304f1c7374');
INSERT INTO client_scopes(id, timestamp, name, client_id) VALUES ('24c1a086-2c84-491c-9ce8-7104a208acd1', NOW(), 'profile', '65601a6c-f249-4058-a4d2-b0304f1c7374');

INSERT INTO client_scopes(id, timestamp, name, client_id) VALUES ('572028d0-d7e7-442f-bcbf-39f09b374ba2', NOW(), 'openid', '13467961-3d7f-43bf-9cbd-19c7199977de');
INSERT INTO client_scopes(id, timestamp, name, client_id) VALUES ('248f28e0-78a8-4e6f-a860-476dde2a04e7', NOW(), 'email', '13467961-3d7f-43bf-9cbd-19c7199977de');
INSERT INTO client_scopes(id, timestamp, name, client_id) VALUES ('cc7b9e7c-9d8d-4f4b-9c34-818400e341c2', NOW(), 'profile', '13467961-3d7f-43bf-9cbd-19c7199977de');
INSERT INTO client_scopes(id, timestamp, name, client_id) VALUES ('8c391c74-dcad-4093-8936-8a4b913762ff', NOW(), 'admin', '13467961-3d7f-43bf-9cbd-19c7199977de');

INSERT INTO users(id, timestamp, email, first_name, last_name, username) VALUES ('d310bd49-6931-4939-b7d5-781e337ec9c0', NOW(), 'user@mail.com', 'User', 'Someone', 'someone');
INSERT INTO user_attributes(id, timestamp, attr_key, type, attr_value, user_id) VALUES ('e153f4a9-2a4f-4b45-96a6-5cd06c55c0de', NOW(), 'phone', 'STRING', '+38640-555-777', 'd310bd49-6931-4939-b7d5-781e337ec9c0');
INSERT INTO user_credentials(id, timestamp, secret, type, user_id) VALUES ('6bdd8c4c-f96e-4d1a-891f-23fa9f643955', NOW(), '$2a$12$2P1QxHgwIoHimCAKw0f/4.6tcft8bk6CJpfeuRKeUTupwVaDx85Ee', 'PASSWORD', 'd310bd49-6931-4939-b7d5-781e337ec9c0');

INSERT INTO settings(id, timestamp, settings_key, type, settings_value) VALUES ('44b773c6-cff6-4865-97a1-43f7156f6ff8', NOW(), 'static.config.registration.enabled', 'BOOLEAN', 'false');
