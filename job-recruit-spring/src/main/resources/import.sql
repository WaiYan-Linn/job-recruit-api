insert into Account(id,email,password,role,phone,activated) values ( '11111111-1111-1111-1111-111111111111','waiyan@gmail.com','$2a$10$yxOkpztjvGkiHrNdQoxkr.GEB9I/HZOgRHJ8PZR1.fi9B5xqI2P/q','JOBSEEKER','09778206157',true);
insert into Account(id,email,password,role,phone,activated) values ( '22222222-2222-2222-2222-222222222222','lotto@gmail.com','$2a$10$qZeq7Kl6WPNTyMXeueK3sORJDAWtN92ZsKj4v6Ipe5rr9EEAdshFe','EMPLOYER','09985708315',true);
insert into Employer(account_id,company_name,website) values ( '22222222-2222-2222-2222-222222222222','Tech Solutions Ltd','https://lotto.com');
insert into job_seeker(account_id,personal_name,resume_url,profile_summary) values ( '11111111-1111-1111-1111-111111111111','Wai Yan','https://waiyan.com','https://hello.com');

