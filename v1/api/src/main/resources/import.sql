INSERT INTO clients(id, timestamp, client_id, name, redirect_uris, secret, status) VALUES('d624096c-0a58-41e1-8ab0-8a16149e8771', NOW(), 'testni', 'Testni', 'http://localhost:4200;http://localhost:8080', '09a2bdc2-4a44-461a-a690-14422cd17308', 'ENABLED');

INSERT INTO users(id, timestamp, email, first_name, last_name, username) VALUES ('d310bd49-6931-4939-b7d5-781e337ec9c0', NOW(), 'user@mail.com', 'User', 'Someone', 'someone');
INSERT INTO user_attributes(id, timestamp, attr_key, type, attr_value, user_id) VALUES ('e153f4a9-2a4f-4b45-96a6-5cd06c55c0de', NOW(), 'phone', 'STRING', '+38640-555-777', 'd310bd49-6931-4939-b7d5-781e337ec9c0');
INSERT INTO user_credentials(id, timestamp, secret, type, user_id) VALUES ('6bdd8c4c-f96e-4d1a-891f-23fa9f643955', NOW(), '$2a$12$2P1QxHgwIoHimCAKw0f/4.6tcft8bk6CJpfeuRKeUTupwVaDx85Ee', 'PASSWORD', 'd310bd49-6931-4939-b7d5-781e337ec9c0');
