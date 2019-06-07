function generateIntervalPlot()

%Algoritmo1
%pq: 0.47897
%pq_1: 0.47897
%pq_2: 0.47897
%p1 cloud: 0.4898
%p2 cloud: 0.5102
%E[T]_CLOUDLET generato analiticamente dalla catena di Markov: 2.9781
%E[T1]_CLOUDLET generato analiticamente dalla catena di Markov: 2.2222
%E[T2]_CLOUDLET generato analiticamente dalla catena di Markov: 3.7037
%E[T]_CLOUD generato analiticamente dalla catena di Markov: 4.2783
%E[T1]_CLOUD generato analiticamente dalla catena di Markov: 4
%E[T2]_CLOUD generato analiticamente dalla catena di Markov: 4.5455
%E[T]_SISTEMA generato analiticamente dalla catena di Markov: 3.6008
%E[T1]_SISTEMA generato analiticamente dalla catena di Markov: 3.0737
%E[T2]_SISTEMA generato analiticamente dalla catena di Markov: 4.10699

%Algoritmo2
%pq: 0.50308
%pq_1: 0.027388
%pq_2: 0.95975
%p1 cloud: 0.026665
%p2 cloud: 0.97333
%E[T]_CLOUDLET generato analiticamente dalla catena di Markov: 2.2901
%E[T1]_CLOUDLET generato analiticamente dalla catena di Markov: 2.2222
%E[T2]_CLOUDLET generato analiticamente dalla catena di Markov: 3.7037
%E[T]_CLOUD generato analiticamente dalla catena di Markov: 4.5309
%E[T1]_CLOUD generato analiticamente dalla catena di Markov: 4
%E[T2]_CLOUD generato analiticamente dalla catena di Markov: 4.5455
%E[T]_SISTEMA generato analiticamente dalla catena di Markov: 3.4174
%E[T1]_SISTEMA generato analiticamente dalla catena di Markov: 2.2709
%E[T2]_SISTEMA generato analiticamente dalla catena di Markov: 4.5116

request = input('Quale simulare vuoi elaborare? (1=transiente; 2=batch): ');
if(request == 1)
    mainDir = 'transient';
elseif(request == 2)
    mainDir = 'batch';
end

%apro la cartella principale
dinfo = dir(fullfile(mainDir));
subdir = dinfo([dinfo.isdir]);
subdir(1:2) = [];%get rid of all directories including . and ..

request1 = input("Quale algoritmo vuoi plottare? (1/2):  ");
if(request1 == 1)
    %apro la sotto cartella per algoritmo
    newDir = strcat(mainDir ,'/', subdir(1).name);
else
    newDir = strcat(mainDir ,'/', subdir(2).name);
end
estimateDir = dir(fullfile(newDir));
estimateDir(1:2) = [];
numDIR = length(estimateDir);

for b=1:numDIR
    newDir2 = strcat(newDir, '/', estimateDir(b).name);
    localDir = dir(newDir2);
    localDir([localDir.isdir]) = [];
   
    if(~isempty(localDir))
        
        nva = length(localDir); %numero di file attesi
        
        cloudlet = zeros(nva,2);
        cloudlet_task1 = zeros(nva,2);
        cloudlet_task2 = zeros(nva,2);
        
        cloud = zeros(nva,2);
        cloud_task1 = zeros(nva,2);
        cloud_task2 = zeros(nva,2);
        
        system = zeros(nva,2);
        system_task1 = zeros(nva,2);
        system_task2 = zeros(nva,2);
        
        
        directory = strcat(newDir2,'/');
        if(request1 == 1)
            elaborateFile(localDir,nva,'Alg1',directory,cloudlet,cloudlet_task1,cloudlet_task2,cloud,cloud_task1,cloud_task2,system,system_task1,system_task2)
        elseif(request1 == 2)
            elaborateFile(localDir,nva,'Alg2',directory,cloudlet,cloudlet_task1,cloudlet_task2,cloud,cloud_task1,cloud_task2,system,system_task1,system_task2)
        end
    end
end


end

function elaborateFile(dinfo,nva,type,directory,cloudlet,cloudlet_task1,cloudlet_task2,cloud,cloud_task1,cloud_task2,system,system_task1,system_task2)
labels = strings(nva,1);

j=1;
for i=1:nva
    %seleziono i files per il calcolo dell'intervallo di confidenza
    if( strfind(dinfo(i).name, 'estimate')==1)
        if ( contains(dinfo(i).name,type) )
            filename = fullfile(directory, dinfo(i).name);
            
            temp = importIntervalText(filename);
            labels(j) = dinfo(i).name;
            
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

labels

X=1:nva;

%%PLOT CLOUDLET
figure('Name','Cloudlet');
errorbar(X, cloudlet(:,1), cloudlet(:,2), 'blackx');xlim([0,j]);
if ( strcmp( type,'Alg1') )
    yline(2.9781, 'Color', 'red', 'LineStyle','-'); %media
elseif ( strcmp( type,'Alg2') )
    yline(2.2901, 'Color', 'red', 'LineStyle','-'); %media
end
if(strcmp( directory,'batch'))
    lgd=legend('tempi medi simulati', 'tempo medio teorico');
    lgd.Title.String = "CLOUDLET";
    ylabel('Tempo medio di risposta');
    xlabel ('tempi di batch');
elseif(strcmp( directory,'transient/estimate'))
    lgd=legend(labels);
    lgd.Title.String = "CLOUDLET";
    %ylabel('Tempo medio di risposta');
    %xlabel ('tempi di batch');
end

figure('Name','Cloudlet_task1');
errorbar(X, cloudlet_task1(:,1), cloudlet_task1(:,2), 'blackx');xlim([0,j+1]);
if (strcmp( type,'Alg1') )
    yline(2.2222, 'Color', 'red', 'LineStyle','-');
elseif ( strcmp( type,'Alg2') )
    yline(2.2222, 'Color', 'red', 'LineStyle','-');
end
if(strcmp( directory,'batch'))
    lgd=legend('tempi medi simulati', 'tempo medio teorico');
    lgd.Title.String = "CLOUDLET TASK1";
    ylabel('Tempo medio di risposta');
    xlabel ('tempi di batch');
elseif(strcmp( directory,'transient/estimate'))
    lgd=legend(labels);
    lgd.Title.String = "CLOUDLET TASK1";
    %ylabel('Tempo medio di risposta');
    %xlabel ('tempi di batch');
end



figure('Name','Cloudlet_task2');
errorbar(X, cloudlet_task2(:,1), cloudlet_task2(:,2), 'blackx');xlim([0,j+1]);
if ( strcmp( type,'Alg1') )
    yline( 3.7037, 'Color', 'red', 'LineStyle','-');
elseif ( strcmp( type,'Alg2') )
    yline( 3.7037, 'Color', 'red', 'LineStyle','-');
end
if(strcmp( directory,'batch'))
    lgd=legend('tempi medi simulati', 'tempo medio teorico');
    lgd.Title.String = "CLOUDLET TASK2";
    ylabel('Tempo medio di risposta');
    xlabel ('tempi di batch');
elseif(strcmp( directory,'transient/estimate'))
    lgd=legend(labels);
    lgd.Title.String = "CLOUDLET TASK2";
    %ylabel('Tempo medio di risposta');
    %xlabel ('tempi di batch');
end



%%PLOT CLOUD
figure('Name','Cloud');
errorbar(X, cloud(:,1), cloud(:,2), 'blackx');xlim([0,j+1]);
if ( strcmp( type,'Alg1') )
    yline(4.2783, 'Color', 'red', 'LineStyle','-'); %media
elseif( strcmp( type,'Alg2') )
    yline(4.5309, 'Color', 'red', 'LineStyle','-'); %media
end
if(strcmp( directory,'batch'))
    lgd=legend('tempi medi simulati', 'tempo medio teorico');
    lgd.Title.String = "CLOUD";
    ylabel('Tempo medio di risposta');
    xlabel ('tempi di batch');
elseif(strcmp( directory,'transient/estimate'))
    lgd=legend(labels);
    lgd.Title.String = "CLOUD";
    %ylabel('Tempo medio di risposta');
    %xlabel ('tempi di batch');
end


figure('Name','Cloud_task1');
errorbar(X, cloud_task1(:,1), cloud_task1(:,2), 'blackx');xlim([0,j+1]);
if ( strcmp( type,'Alg1') )
    yline(4, 'Color', 'red', 'LineStyle','-'); %media
elseif ( strcmp( type,'Alg2') )
    yline(4, 'Color', 'red', 'LineStyle','-'); %media
end
if(strcmp( directory,'batch'))
    lgd=legend('tempi medi simulati', 'tempo medio teorico');
    lgd.Title.String = "CLOUD TASK1";
    ylabel('Tempo medio di risposta');
    xlabel ('tempi di batch');
elseif(strcmp( directory,'transient/estimate'))
    lgd=legend(labels);
    lgd.Title.String = "CLOUD TASK1";
    %ylabel('Tempo medio di risposta');
    %xlabel ('tempi di batch');
end


figure('Name','Cloud_task2');
errorbar(X, cloud_task2(:,1), cloud_task2(:,2), 'blackx');xlim([0,j+1]);
if ( strcmp( type,'Alg1') )
    yline(4.5455, 'Color', 'red', 'LineStyle','-'); %media
elseif ( strcmp( type,'Alg2') )
    yline(4.5455, 'Color', 'red', 'LineStyle','-'); %media
end
if(strcmp( directory,'batch'))
    lgd=legend('tempi medi simulati', 'tempo medio teorico');
    lgd.Title.String = "CLOUD TASK2";
    ylabel('Tempo medio di risposta');
    xlabel ('tempi di batch');
elseif(strcmp( directory,'transient/estimate'))
    lgd=legend(labels);
    lgd.Title.String = "CLOUD TASK2";
    %ylabel('Tempo medio di risposta');
    %xlabel ('tempi di batch');
end



%%PLOT SYSTEM
figure('Name','System');
errorbar(X, system(:,1), system(:,2), 'blackx');xlim([0,j+1]);
if ( strcmp( type,'Alg1') )
    yline(3.6008, 'Color', 'red', 'LineStyle','-'); %media
elseif ( strcmp( type,'Alg2') )
    yline(3.4174, 'Color', 'red', 'LineStyle','-'); %media
end
if(strcmp( directory,'batch'))
    lgd=legend('tempi medi simulati', 'tempo medio teorico');
    lgd.Title.String = "SYSTEM";
    ylabel('Tempo medio di risposta');
    xlabel ('tempi di batch');
elseif(strcmp( directory,'transient/estimate'))
    lgd=legend('tempi medi simulati', 'tempo medio teorico');
    lgd.Title.String = "SYSTEM";
    %ylabel('Tempo medio di risposta');
    %xlabel ('tempi di batch');
end


figure('Name','System_task1');
errorbar(X, system_task1(:,1), system_task1(:,2), 'blackx');xlim([0,j+1]);
if ( strcmp( type,'Alg1') )
    yline(3.0737, 'Color', 'red', 'LineStyle','-'); %media
elseif( strcmp( type,'Alg2') )
    yline(2.2709, 'Color', 'red', 'LineStyle','-'); %media
end
if(strcmp( directory,'batch'))
    lgd=legend('tempi medi simulati', 'tempo medio teorico');
    lgd.Title.String = "SYSTEM TASK1";
    ylabel('Tempo medio di risposta');
    xlabel ('tempi di batch');
elseif(strcmp( directory,'transient/estimate'))
    lgd=legend('tempi medi simulati', 'tempo medio teorico');
    lgd.Title.String = "SYSTEM TASK1";
    %ylabel('Tempo medio di risposta');
    %xlabel ('tempi di batch');
end

figure('Name','System_task2');
errorbar(X, system_task2(:,1), system_task2(:,2), 'blackx');xlim([0,j+1]);
if ( strcmp( type,'Alg1') )
    yline(4.10699, 'Color', 'red', 'LineStyle','-'); %media
elseif( strcmp( type,'Alg2') )
    yline(4.5116, 'Color', 'red', 'LineStyle','-'); %media
end
if(strcmp( directory,'batch'))
    lgd=legend('tempi medi simulati', 'tempo medio teorico');
    lgd.Title.String = "SYSTEM TASK2";
    ylabel('Tempo medio di risposta');
    xlabel ('tempi di batch');
elseif(strcmp( directory,'transient/estimate'))
    lgd=legend('tempi medi simulati', 'tempo medio teorico');
    lgd.Title.String = "SYSTEM TASK2";
    %ylabel('Tempo medio di risposta');
    %xlabel ('tempi di batch');
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

