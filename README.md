# Notification service

## How to start tests?
You need to run the following command in the terminal:

``
    mvn test
``

## How to create credentials for SMTP server?

Google SMTP server was used for the email sending in the project, therefore here will be a short tutorial about how
you can create required credentials, so notification-service could send messages on your email.

### Two-factor authentication

First of all, you need to turn on the two-factor authentication for your Google account.
Here is a guide how to do [that](https://support.google.com/accounts/answer/185839).

### Generate a password

Since your account is secured you can generate a special password, so your application could be able to send emails.
You can follow [this link](https://myaccount.google.com/apppasswords) for these purposes. On that page, you'll be required
to come up with application name (any name possible), then you will get the 16 character password, it can look like this
(the password on the picture is not active). 

![password-image](https://res.cloudinary.com/dbkgbcqcf/image/upload/v1719405238/%D0%97%D0%BD%D1%96%D0%BC%D0%BE%D0%BA_%D0%B5%D0%BA%D1%80%D0%B0%D0%BD%D0%B0_2024-06-26_153219_wz5i22.png)

Save this passport, it is essential for our app.

### Set credentials

Once you get a passport, you are almost ready to start the service, although there are couple more thing left.
Find '.env' file in the root directory of a project.

![env-file-content-image](https://res.cloudinary.com/dbkgbcqcf/image/upload/v1719405585/%D0%97%D0%BD%D1%96%D0%BC%D0%BE%D0%BA_%D0%B5%D0%BA%D1%80%D0%B0%D0%BD%D0%B0_2024-06-26_153916_fx0e4y.png)

Now you are able to see the content of this file. Change fields

* 'EMAIL_HOST' -  field should not be changed;
* 'EMAIL_USERNAME' - should be replaced with a name of your email. Notice that your email should be from the account,
where you received a password (Example: admin@mail.com).
* 'EMAIL_PASSWORD' - should be replaced with a 16 character password that you received during previous steps.
* 'EMAIL_PORT' - should have value '587'

Congrats! After all these steps you are ready to start a program.

## How to start a project?
You need to run the following command in the terminal:

``
docker-compose up --build
``

It is important to mention that this docker-compose project
relies on external network from the [book-project](https://github.com/Wow11One/book-rest-api).
That's why you need to start book-project first, so notification-service could work.