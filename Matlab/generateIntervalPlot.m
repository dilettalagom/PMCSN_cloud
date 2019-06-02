function generateIntervalPlot()

nva = 7; %NUMERO ATTESO DI FILES

%apro la cartella seed
dinfo = dir(fullfile('batch'));
dinfo([dinfo.isdir]) = [];     %get rid of all directories including . and ..
nfiles = length(dinfo);


cloudlet = zeros(nva,2);
cloudlet_task1 = zeros(nva,2);
cloudlet_task2 = zeros(nva,2);

cloud = zeros(nva,2);
cloud_task1 = zeros(nva,2);
cloud_task2 = zeros(nva,2);

system = zeros(nva,2);
system_task1 = zeros(nva,2);
system_task2 = zeros(nva,2);

label = strings(nva);
%label = erase(label,'estimateFile');
%label = erase(label,'.csv');

j=1;
for i=1: nfiles
    %seleziono i files per il calcolo dell'intervallo di confidenza
    if( strfind(dinfo(i).name, 'estimate')==true )
        filename = fullfile('batch', dinfo(i).name);
        label(j)= dinfo(i).name;
        
        temp = importIntervalText(filename);
        
        format long
        %cloudlet
        cloudlet(j,1) = str2double(temp.cloudlet);
        cloudlet(j,2) = str2double(temp.VarName2);
        
        cloudlet_task1(j,1) = str2double(temp.cloudlet_task1);
        cloudlet_task1(j,2) = str2double(temp.VarName4);
        
        cloudlet_task2(j,1) = str2double(temp.cloudlet_task2);
        cloudlet_task2(j,2)= str2double(temp.VarName6);
        
        %cloud
        cloud(j,1) = str2double(temp.cloud);
        cloud(j,2) = str2double(temp.VarName8);
        
        cloud_task1(j,1) = str2double(temp.cloud_task1);
        cloud_task1(j,2) = str2double(temp.VarName10);
        
        cloud_task2(j,1) = str2double(temp.cloud_task2);
        cloud_task2(j,2) = str2double(temp.VarName12);
        
        %system
        system(j,1) = str2double(temp.system);
        system(j,2) = str2double(temp.VarName14);
        
        system_task1(j,1) = str2double(temp.system_task1);
        system_task1(j,2) = str2double(temp.VarName16);
        
        system_task2(j,1) = str2double(temp.system_task2);
        system_task2(j,2) = str2double(temp.VarName18);
        j=j+1;
    end
end


X=1:nva;

%%PLOT CLOUDLET
figure('Name','Cloudlet');
errorbar(X, cloudlet(:,1), cloudlet(:,2), 'blackx');xlim([0,nva+1]); 
yline(1.5517, 'Color', 'red', 'LineStyle','-'); %media

figure('Name','Cloudlet_task1');
errorbar(X, cloudlet_task1(:,1), cloudlet_task1(:,2), 'blackx');xlim([0,nva+1]);

figure('Name','Cloudlet_task2');
errorbar(X, cloudlet_task2(:,1), cloudlet_task2(:,2), 'blackx');xlim([0,nva+1]);


%%PLOT CLOUD
figure('Name','Cloud');
errorbar(X, cloud(:,1), cloud(:,2), 'blackx');xlim([0,nva+1]);
yline(0.11256, 'Color', 'red', 'LineStyle','-'); %media

figure('Name','Cloud_task1');
errorbar(X, cloud_task1(:,1), cloud_task1(:,2), 'blackx');xlim([0,nva+1]);

figure('Name','Cloud_task2');
errorbar(X, cloud_task2(:,1), cloud_task2(:,2), 'blackx');xlim([0,nva+1]);

%%PLOT SYSTEM
figure('Name','System');
errorbar(X, system(:,1), system(:,2), 'blackx');xlim([0,nva+1]);
yline(1.6642, 'Color', 'red', 'LineStyle','-'); %media

figure('Name','System_task1');
errorbar(X, system_task1(:,1), system_task1(:,2), 'blackx');xlim([0,nva+1]);

figure('Name','System_task2');
errorbar(X, system_task2(:,1), system_task2(:,2), 'blackx');xlim([0,nva+1]);



end

%%Import file
function text = importIntervalText(filename, startRow, endRow)
% Initialize variables.
delimiter = ';';
if nargin<=2
    startRow = 2;
    endRow = inf;
end
% Format for each line of text:
formatSpec = '%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%[^\n\r]';

% Open the text file.
fileID = fopen(filename,'r');
% Read columns of data according to the format.
dataArray = textscan(fileID, formatSpec, endRow(1)-startRow(1)+1, 'Delimiter', delimiter, 'TextType', 'string', 'HeaderLines', startRow(1)-1, 'ReturnOnError', false, 'EndOfLine', '\r\n');
for block=2:length(startRow)
    frewind(fileID);
    dataArrayBlock = textscan(fileID, formatSpec, endRow(block)-startRow(block)+1, 'Delimiter', delimiter, 'TextType', 'string', 'HeaderLines', startRow(block)-1, 'ReturnOnError', false, 'EndOfLine', '\r\n');
    for col=1:length(dataArray)
        dataArray{col} = [dataArray{col};dataArrayBlock{col}];
    end
end
% Close the text file.
fclose(fileID);

% Create output variable
text = table(dataArray{1:end-1}, 'VariableNames', {'cloudlet','VarName2','cloudlet_task1','VarName4','cloudlet_task2','VarName6','cloud','VarName8','cloud_task1','VarName10','cloud_task2','VarName12','system','VarName14','system_task1','VarName16','system_task2','VarName18'});
end

