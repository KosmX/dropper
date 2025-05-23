# dropper
A simple file sharing service with a mobile based single-upload authentication

This is partially [KMP](https://viaubxav084.github.io/laborok/) homework. (for my university)

Dropper solves a very simple problem I constantly have: how to move a file from a random computer to my PC without logging in or having a physical USB.  
It requires a self-hosted server, so it is for me and my friends, but anyone who deploys their own server can use it. TODO guide on how-to.  

This app has two completely distinct interfaces, the user and the admin interface. The user interface is a simple HTML+CSS+JS program,
it should work on all browsers (even chrome). This interface has two features: having a QR code for identification, and, **if authorized** can upload one or more files.  
  
The second interface: admin interface. This is going to be an Android app, but using KMP,
this interface will also target desktop (java with special care for Linux+Wayland) and browser using Kotlin's WASM.  

It could target iOS, but I don't Apple...

This app also requires a backend server, this is a Ktor web server.  

## Usage

### Main screen on admin
The welcome screen is just a few shortcuts to different app features. Basically a "what do you need": There will be 6 tiles, scan QR, create link, 
manage live sessions, manage shares, upload directly, and an advanced admin stuff.  

### Route #1, shared links
This way is a more common way, but is going to be needed.   

First, the admin app logs in (just open the app, as it will remember auth tokens). Open the second (create link) tab, it will display a config where multiple things can be set:
- max file size
- single file or folder allowed
- single use, or multiuse
- time to live (expiration)
- open share or whether the uploaded files can be downloaded using the same link, or only the admin can download.

Once the configs are set, a share button is on the bottom, pressing that will copy the link and display a QR code. There is a back button, pressing that will open a tab where the session can be edited.  
The link should be shared with the source device. (sent on an instan messaging app like Matrix, or just copied over by hand)

If a user uses the link, they can upload one or more files to the server. Once a file is uploaded, it will be displayed in the app. This file can also be downloaded.

### Route #2, single-time uploadd auth
This is the interesting one.
First, the webserver domain is typed into a browser **on the source machine**  
Remember, this machine has internet, but it may be infected by malware or is the computer of somebody else. Logging in on this machine is not possible.

When the domain is typed in, a random ID will be assigned to that instance, and a biiig QR code will be displayed on the site. (this QR code is server-generated, as I want the webpage to work even without JS)  
Now comes the admin device. It scans the QR code (either from whithin the app, or using any QR code scanner, or the ID is dictated over).  
When the device has the correct ID, the share config screen will be opened (almost like before). Typical things can be set, if everything is OK, press ALLOW.  

When the session is authorized, the source uploader page will refresh itself (eithet using websockets, or manually by pressing F5). This time, instead of a QR code, a choose file menu will appear.  

On the admin interface, ALLOW will redirect to the view session menu, where any uploaded file will be shown.


### Manage live sessions:
it is just a menu, where all active shares (links) are displayed who can upload to the server. Sessions from here can be quickly de-activated, deleted, or the permissions can be changed.  

### Manage shares
A menu displaying all uploaded assets. (if multiple files were uploaded from a single session, those files will be displayed in one group). Files can be downloaded, deleted, shared from here.  
If a session expires without uploading a single file, it will disappear completely, no empty entry will be created.   

### upload directly
if you want to upload a file from an admin device, this is where you can do it. Or you can create yourself a session.   

### Misc admin stuff
probably server logs, emergency lockdown and other utilities will be accessible from here.

# Technologies
- [Kotlin](https://kotlinlang.org/) for the admin client and server. Also kotlin standard libraries
- [Kotlin Multiplatform](https://www.jetbrains.com/kotlin-multiplatform/) for the admin client
- [Ktor](https://ktor.io/) for the web-server. Both the REST&ASYNC API, and the hosting of the user client.
- [Exposed](https://www.jetbrains.com/exposed/) for the server, an ORM lib I'm already familiar
- [PostgreSQL](https://www.postgresql.org/) for the database on the server.
- HTTP+CSS(+JS) for the simple client.
- QR-kit
