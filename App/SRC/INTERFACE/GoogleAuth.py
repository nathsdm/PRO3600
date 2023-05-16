import os.path

import io

from apiclient import errors
from apiclient import http

from google.auth.transport.requests import Request
from google.oauth2.credentials import Credentials
from google_auth_oauthlib.flow import InstalledAppFlow
from googleapiclient.discovery import build
from googleapiclient.errors import HttpError
from googleapiclient.http import MediaIoBaseDownload

from googleapiclient.http import MediaFileUpload

import tkinter as tk


class GoogleAuth:
    def __init__(self, master):
        self.master = master
        self.creds = None
        self.file_id = None
        self.path = os.path.join("App", "DATA", "CARDS", "cards.txt")
        self.local_fd = io.FileIO(self.path, mode="w")
        # If modifying these scopes, delete the file token.json.
        self.SCOPES = ['https://www.googleapis.com/auth/drive.appdata']
        # The file token.json stores the user's access and refresh tokens, and is
        # created automatically when the authorization flow completes for the first
        # time.
        if os.path.exists(os.path.join("App", "token.json")):
            self.creds = Credentials.from_authorized_user_file(os.path.join("App", "token.json"), self.SCOPES)
        # If there are no (valid) credentials available, let the user log in.
        if not self.creds or not self.creds.valid:
            self.master.withdraw()
            if self.creds and self.creds.expired and self.creds.refresh_token:
                try:
                    self.creds.refresh(Request())
                except:
                    tk.messagebox.showerror(title="Error", message="An error occured while refreshing the token. Check your connection and please try again.")
                    os.remove(path=os.path.join("App", "token.json"))
                    self.master.destroy()
                    exit()
                    return
                    
            else:
                flow = InstalledAppFlow.from_client_secrets_file(os.path.join("App", "credentials.json"), self.SCOPES)
                self.creds = flow.run_local_server(port=0)
            self.master.deiconify()
            # Save the credentials for the next run
            with open(os.path.join("App", "token.json"), 'w') as token:
                token.write(self.creds.to_json())
        self.service = build('drive', 'v3', credentials=self.creds)
        try:
            list_files = self.list_appdata()
            if not "CARDS.txt" in [file.get('name') for file in list_files]:
                self.file_id = self.upload_appdata()
                self.download_file()
            else:
                self.file_id = [file.get('id') for file in list_files if file.get('name') == "CARDS.txt"][0]
                self.download_file()
            self.get_collections()
        except HttpError as error:
            tk.messagebox.showerror(title="Error", message="An error occured while downloading the file. Check your connection and please try again.")
            print(f'An error occurred: {error}')

    def get_collections(self):
        done = False
        collection_id = 0
        while not done:
            list_files = self.list_appdata()
            if not F"COLLECTION_{collection_id}.txt" in [file.get('name') for file in list_files]:
                done = True
            else:
                self.path = os.path.join("App", "DATA", "CARDS", F"COLLECTION_{collection_id}.txt")
                self.local_fd = io.FileIO(self.path, mode="w")
                self.file_id = [file.get('id') for file in list_files if file.get('name') == F"COLLECTION_{collection_id}.txt"][0]
                self.download_file()
                collection_id += 1
            
    
    def upload_appdata(self):
        """
        Insert a file in the application data folder and prints file Id.
        Returns : ID's of the inserted files
        """

        try:
            # pylint: disable=maybe-no-member
            file_metadata = {
                'name': 'CARDS.txt',
                'parents': ['appDataFolder']
            }
            media = MediaFileUpload(self.path,
                                    mimetype='text/txt',
                                    resumable=True)
            file = self.service.files().create(body=file_metadata, media_body=media,
                                        fields='id').execute()
            print(F'File ID: {file.get("id")}')

        except HttpError as error:
            print(F'An error occurred: {error}')
            file = None

        return file.get('id')

    def download_file(self):
        """
        Download a Drive file's content to the local filesystem.

        Args:
        service: Drive API Service instance.
        file_id: ID of the Drive file that will downloaded.
        local_fd: io.Base or file object, the stream that the Drive file's
            contents will be written to.
        """
        request = self.service.files().get_media(fileId=self.file_id)
        media_request = http.MediaIoBaseDownload(self.local_fd, request)

        while True:
            try:
                download_progress, done = media_request.next_chunk()
            except errors.HttpError as error:
                print('An error occurred: %s' % error)
                return
            if download_progress:
                print('Download Progress: %d%%' % int(download_progress.progress() * 100))
            if done:
                print('Download Complete')
                return

    def update_filedata(self):
        self.service.files().update(fileId=self.file_id, media_body=MediaFileUpload(os.path.join("App", "DATA", "CARDS", "cards.txt"), mimetype='text/txt', resumable=True)).execute()
        for filename in os.listdir(os.path.join("App", "DATA", "COLLECTIONS")):
            if filename.startswith("COLLECTION_"):
                self.file_id = [file.get('id') for file in self.list_appdata() if file.get('name') == filename]
                if self.file_id:
                    self.file_id = self.file_id[0]
                    self.service.files().update(fileId=self.file_id, media_body=MediaFileUpload(os.path.join("App", "DATA", "CARDS", filename), mimetype='text/txt', resumable=True)).execute()
                else:
                    try:
                        file_metadata = {
                            'name': filename,
                            'parents': ['appDataFolder']
                        }
                        media = MediaFileUpload(os.path.join("App", "DATA", "COLECTIONS", filename),
                                                mimetype='text/txt',
                                                resumable=True)
                        file = self.service.files().create(body=file_metadata, media_body=media,
                                                    fields='id').execute()
                    except HttpError as error:
                        print(F'An error occurred: {error}')
                        file = None
                        
    def list_appdata(self):
        """
        List all files inserted in the application data folder
        prints file titles with Ids.
        Returns : List of items

        Load pre-authorized user credentials from the environment.
        """

        try:
            # pylint: disable=maybe-no-member
            response = self.service.files().list(spaces='appDataFolder',
                                            fields='nextPageToken, files(id, '
                                                'name)', pageSize=10).execute()
            for file in response.get('files', []):
                # Process change
                print(F'Found file: {file.get("name")}, {file.get("id")}')

        except HttpError as error:
            print(F'An error occurred: {error}')
            response = None

        return response.get('files')