# notion-clone

Having a go at the ["Build your own Notion"  coding challenge](https://codingchallenges.fyi/challenges/challenge-notion)

So far the authentication logic has been implemented, using passkeys and the Java Yubico library to generate public-private keys, challenges to be signed, provide attestation.


The following chart describes the process flow during register (sign-up):

![image](https://github.com/user-attachments/assets/b0b771dc-6315-4615-8920-bb9728cd27a7)


The following chart describes the process flow during authentication (login):

![image](https://github.com/user-attachments/assets/258eae10-1862-4aac-92ad-a14d7345fef3)

Source for both images: https://www.corbado.com/blog/passkey-tutorial-how-to-implement-passkeys

