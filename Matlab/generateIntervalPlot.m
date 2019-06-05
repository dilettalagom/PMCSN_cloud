function generateIntervalPlot()

%Algoritmo1
%E[T]_CLOUDLET generato analiticamente dalla catena di Markov: 1.5517
%E[T1]_CLOUDLET generato analiticamente dalla catena di Markov: 1.1578
%E[T2]_CLOUDLET generato analiticamente dalla catena di Markov: 1.9297
%E[T]_CtotalTask
%E[T1]_CLOUD generato analiticamente dalla catena di Markov: 0.11974
%E[T2]_CLOUD generato analiticamente dalla catena di Markov: 0.10537
%E[T]_SISTEMA generato analiticamente dalla catena di Markov: 1.6642
%E[T1]_SISTEMA generato analiticamente dalla catena di Markov: 1.2776
%E[T2]_SISTEMA generato analiticamente dalla catena di Markov: 2.0351


%Algoritmo2
%E[T]_CLOUDLET generato analiticamente dalla catena di Markov: 1.5711
%E[T1]_CLOUDLET generato analiticamente dalla catena di Markov: 0.21261
%E[T2]_CLOUDLET generato analiticamente dalla catena di Markov: 2.8752
%E[T]_CLOUD generato analiticamente dalla catena di Markov: 0.068514
%E[T1]_CLOUD generato analiticamente dalla catena di Markov: 0.072887
%E[T2]_CLOUD generato analiticamente dalla catena di Markov: 0.064141
%E[T]_SISTEMA generato analiticamente dalla catena di Markov: 1.6396
%E[T1]_SISTEMA generato analiticamente dalla catena di Markov: 0.28549
%E[T2]_SISTEMA generato analiticamente dalla catena di Markov: 2.9394

%apro la cartella seed
dinfo = dir(fullfile('batch'));
dinfo([dinfo.isdir]) = [];     %get rid of all directories including . and ..
nva = length(dinfo); %numero di file attesi


cloudlet = zeros(nva,2);
cloudlet_task1 = zeros(nva,2);
cloudlet_task2 = zeros(nva,2);

cloud = zeros(nva,2);
cloud_task1 = zeros(nva,2);
cloud_task2 = zeros(nva,2);

system = zeros(nva,2);
system_task1 = zeros(nva,2);
system_task2 = zeros(nva,2);


elaborateFile(dinfo,nva,'Alg1',cloudlet,cloudlet_task1,cloudlet_task2,cloud,cloud_task1,cloud_task2,system,system_task1,system_task2)

%elaborateFile(dinfo,nva,'Alg2',cloudlet,cloudlet_task1,cloudlet_task2,cloud,cloud_task1,cloud_task2,system,system_task1,system_task2)
end

function elaborateFile(dinfo,nva,type,cloudlet,cloudlet_task1,cloudlet_task2,cloud,cloud_task1,cloud_task2,system,system_task1,system_task2)

j=1;
for i=1: nva
    %seleziono i files per il calcolo dell'intervallo di confidenza
    if( strfind(dinfo(i).name, 'estimate')==true )
        if ( contains(dinfo(i).name,type) )
            filename = fullfile('batch', dinfo(i).name);         

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
end

X=1:nva;

%%PLOT CLOUDLET
figure('Name','Cloudlet');
errorbar(X, cloudlet(:,1), cloudlet(:,2), 'blackx');xlim([0,nva+1]); 
if ( strcmp( type,'Alg1') )
    yline(1.5517, 'Color', 'red', 'LineStyle','-'); %media
elseif ( strcmp( type,'Alg2') )
    yline(1.5711, 'Color', 'red', 'LineStyle','-'); %media
end

figure('Name','Cloudlet_task1');
errorbar(X, cloudlet_task1(:,1), cloudlet_task1(:,2), 'blackx');xlim([0,nva+1]);
if ( strcmp( type,'Alg1') )
    yline(1.1578, 'Color', 'red', 'LineStyle','-');
elseif ( strcmp( type,'Alg2') )
    yline(1.1578, 'Color', 'red', 'LineStyle','-');
end

figure('Name','Cloudlet_task2');
errorbar(X, cloudlet_task2(:,1), cloudlet_task2(:,2), 'blackx');xlim([0,nva+1]);
if ( strcmp( type,'Alg1') )
    yline(1.9297, 'Color', 'red', 'LineStyle','-');
elseif ( strcmp( type,'Alg2') )
    yline(1.9297, 'Color', 'red', 'LineStyle','-');
end


%%PLOT CLOUD
figure('Name','Cloud');
errorbar(X, cloud(:,1), cloud(:,2), 'blackx');xlim([0,nva+1]);
if ( strcmp( type,'Alg1') )
    yline(0.11256, 'Color', 'red', 'LineStyle','-'); %media
elseif( strcmp( type,'Alg2') )
    yline(0.11256, 'Color', 'red', 'LineStyle','-'); %media
end

figure('Name','Cloud_task1');
errorbar(X, cloud_task1(:,1), cloud_task1(:,2), 'blackx');xlim([0,nva+1]);
if ( strcmp( type,'Alg1') )
    yline(0.11974, 'Color', 'red', 'LineStyle','-'); %media
elseif ( strcmp( type,'Alg2') )
    yline(0.11974, 'Color', 'red', 'LineStyle','-'); %media
end

figure('Name','Cloud_task2');
errorbar(X, cloud_task2(:,1), cloud_task2(:,2), 'blackx');xlim([0,nva+1]);
if ( strcmp( type,'Alg1') )
    yline(0.10537, 'Color', 'red', 'LineStyle','-'); %media
elseif ( strcmp( type,'Alg2') )
    yline(0.10537, 'Color', 'red', 'LineStyle','-'); %media
end


%%PLOT SYSTEM
figure('Name','System');
errorbar(X, system(:,1), system(:,2), 'blackx');xlim([0,nva+1]);
if ( strcmp( type,'Alg1') )
    yline(1.6642, 'Color', 'red', 'LineStyle','-'); %media
elseif ( strcmp( type,'Alg2') )
    yline(1.6642, 'Color', 'red', 'LineStyle','-'); %media
end


figure('Name','System_task1');
errorbar(X, system_task1(:,1), system_task1(:,2), 'blackx');xlim([0,nva+1]);
if ( strcmp( type,'Alg1') )
    yline(1.2776, 'Color', 'red', 'LineStyle','-'); %media
elseif( strcmp( type,'Alg2') )
    yline(1.2776, 'Color', 'red', 'LineStyle','-'); %media
end


figure('Name','System_task2');
errorbar(X, system_task2(:,1), system_task2(:,2), 'blackx');xlim([0,nva+1]);
if ( strcmp( type,'Alg1') )
    yline(2.0351, 'Color', 'red', 'LineStyle','-'); %media
elseif( strcmp( type,'Alg2') )
    yline(2.0351, 'Color', 'red', 'LineStyle','-'); %media
end

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

