move "%1" "%1.inprogress"
C:\data\apps\unzip\bin\unzip -o "%1.inprogress" -d %2
del "%1.inprogress"