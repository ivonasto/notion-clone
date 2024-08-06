# Dictionary

* user handle -  represents the mapping of a public key credential to a user account with the Relying Party (RP).
As such, it is the RP that sets its value, user.id. Its characteristics are:
    - Must not contain information that could identify the user
    - Opaque byte sequence, maximum length = 64
    - Must be pseudo random
    - Must not include personal identifying information
    - In practice this translates to the UUID which we transform into bytearray
    - An authenticator maps the user handle to a private key. 
    - Authentication and authorization decisions are made on the basis of the user.id

*