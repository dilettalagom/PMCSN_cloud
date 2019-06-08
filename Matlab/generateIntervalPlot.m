function generateIntervalPlot()


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
%% PLOT CLOUDLET
stamp = figure('Name','Cloudlet');
errorbar(X, cloudlet(:,1), cloudlet(:,2), 'blackx');xlim([0,j]);
if(contains(directory,'Tempi') )
    if ( strcmp( type,'Alg1') )
        yline(2.97808012093726, 'Color', 'red', 'LineStyle','-'); %media
        path = 'figure/Alg1/Cloudlet_time.png';
    elseif ( strcmp( type,'Alg2') )
        yline(2.28344128305216, 'Color', 'red', 'LineStyle','-'); %media
        path = 'figure/Alg2/Cloudlet_time.png';
    end
    lgd=legend('tempi medi simulati', 'tempo medio teorico');
    lgd.Title.String = "CLOUDLET";
    ylabel('Tempo medio di risposta');
    %xlabel ('numero di batch');
    saveas(stamp,path);
    
elseif(contains(directory,'Task'))
    if ( strcmp( type,'Alg1') )
        yline(19.0079214380542, 'Color', 'red', 'LineStyle','-'); %media
        path = 'figure/Alg1/Cloudlet_Num.png';
    elseif ( strcmp( type,'Alg2') )
        yline(13.8997868695146, 'Color', 'red', 'LineStyle','-'); %media
        path = 'figure/Alg2/Cloudlet_Num.png';
    end
    lgd=legend('numero di task', 'numero medio teorico');
    lgd.Title.String = "CLOUDLET";
    ylabel('Numero medio di task');
    %xlabel ('numero di batch');
    saveas(stamp,path);
    
elseif(contains(directory,'Throughput'))
    if ( strcmp( type,'Alg1') )
        yline(6.38260915293038, 'Color', 'red', 'LineStyle','-'); %media
        path = 'figure/Alg1/Cloudlet_Throughput.png';
    elseif ( strcmp( type,'Alg2') )
        yline(6.08721011250853, 'Color', 'red', 'LineStyle','-'); %media
        path ='figure/Alg2/Cloudlet_Throughput.png';
    end
    lgd=legend('Throughput medio', 'Throughput teorico');
    lgd.Title.String = "CLOUDLET";
    ylabel('Throughput medio');
    
    saveas(stamp,path);
end

%% PLOT CLOUDLET_TASK1
stamp1 = figure('Name','Cloudlet_task1');
errorbar(X, cloudlet_task1(:,1), cloudlet_task1(:,2), 'blackx');xlim([0,j]);
if(contains(directory,'Tempi') )
    if ( strcmp( type,'Alg1') )
        yline(2.22222222222222, 'Color', 'red', 'LineStyle','-');
        path = 'figure/Alg1/Cloudlet_time_task1.png';
    elseif ( strcmp( type,'Alg2') )
        yline(2.22222222222222, 'Color', 'red', 'LineStyle','-');
        path = 'figure/Alg2/Cloudlet_time_task1.png';
    end
    lgd=legend('tempi medi simulati', 'tempo medio teorico');
    lgd.Title.String = "CLOUDLET TASK1";
    ylabel('Tempo medio di risposta');
    %xlabel ('numero di batch');
    saveas(stamp1,path);
    
elseif(contains(directory,'Task'))
    if ( strcmp( type,'Alg1') )
        yline(6.94705758142082, 'Color', 'red', 'LineStyle','-');
        path = 'figure/Alg1/Cloudlet_Num_task1.png';
    elseif ( strcmp( type,'Alg2') )
        yline(12.9681536541089, 'Color', 'red', 'LineStyle','-');
        path ='figure/Alg2/Cloudlet_Num_task1.png';
    end
    lgd=legend('numero di task', 'numero medio teorico');
    lgd.Title.String = "CLOUDLET TASK1";
    ylabel('Numero medio di task');
    %xlabel ('numero di batch');
    saveas(stamp1,path);
    
elseif(contains(directory,'Throughput'))
    if ( strcmp( type,'Alg1') )
        yline(3.12617591163937, 'Color', 'red', 'LineStyle','-');
        path ='figure/Alg1/Cloudlet_Throughput_task1.png';
    elseif ( strcmp( type,'Alg2') )
        yline(5.83566914434899, 'Color', 'red', 'LineStyle','-');
        path ='figure/Alg2/Cloudlet_Throughput_task1.png';
    end
    lgd=legend('Throughput medio', 'Throughput teorico');
    lgd.Title.String = "CLOUDLET TASK1";
    ylabel('Throughput medio');
    saveas(stamp1,path);
end


%% PLOT CLOUDLET_TASK2
stamp2 = figure('Name','Cloudlet_task2');
errorbar(X, cloudlet_task2(:,1), cloudlet_task2(:,2), 'blackx');xlim([0,j+1]);

if(contains(directory,'Tempi') )
    if ( strcmp( type,'Alg1') )
        yline(3.7037037037037, 'Color', 'red', 'LineStyle','-');
        path ='figure/Alg1/Cloudlet_time_task2.png';
    elseif ( strcmp( type,'Alg2') )
        yline(3.7037037037037, 'Color', 'red', 'LineStyle','-');
        path ='figure/Alg2/Cloudlet_time_task2.png';
    end
    lgd=legend('tempi medi simulati', 'tempo medio teorico');
    lgd.Title.String = "CLOUDLET TASK2";
    ylabel('Tempo medio di risposta');
    %xlabel ('numero di batch');
    saveas(stamp2,path);
    
elseif(contains(directory,'Task'))
    if ( strcmp( type,'Alg1') )
        yline(12.0608638566334, 'Color', 'red', 'LineStyle','-');
        path = 'figure/Alg1/Cloudlet_Num_task2.png';
    elseif ( strcmp( type,'Alg2') )
        yline(0.931633215405701, 'Color', 'red', 'LineStyle','-');
        path = 'figure/Alg2/Cloudlet_Num_task2.png';
    end
    lgd=legend('numero di task', 'numero medio teorico');
    lgd.Title.String = "CLOUDLET TASK2";
    ylabel('Numero medio di task');
    %xlabel ('numero di batch');
    saveas(stamp2,path);
    
elseif(contains(directory,'Throughput'))
    if ( strcmp( type,'Alg1') )
        yline(3.25643324129101, 'Color', 'red', 'LineStyle','-');
        path ='figure/Alg1/Cloudlet_Throughput_task2.png';
    elseif ( strcmp( type,'Alg2') )
        yline(0.251540968159539, 'Color', 'red', 'LineStyle','-');
        path ='figure/Alg2/Cloudlet_Throughput_task2.png';
    end
    lgd=legend('Throughput medio', 'Throughput teorico');
    lgd.Title.String = "CLOUDLET TASK2";
    ylabel('Throughput medio');
    
    saveas(stamp2,path);
end

%% PLOT CLOUD
stamp3 = figure('Name','Cloud');
errorbar(X, cloud(:,1), cloud(:,2), 'blackx');xlim([0,j+1]);
if(contains(directory,'Tempi') )
    if ( strcmp( type,'Alg1') )
        yline(4.27829313543599, 'Color', 'red', 'LineStyle','-'); %media
        path ='figure/Alg1/Cloud_time.png';
    elseif ( strcmp( type,'Alg2') )
        yline(4.53090999439737, 'Color', 'red', 'LineStyle','-'); %media
        path ='figure/Alg2/Cloud_time.png';
    end
     lgd=legend('tempi medi simulati', 'tempo medio teorico');
    lgd.Title.String = "CLOUD";
    ylabel('Tempo medio di risposta');
    %xlabel ('numero di batch');
    saveas(stamp3,path);
    
elseif(contains(directory,'Task'))
    if ( strcmp( type,'Alg1') )
        yline(25.1024179839379, 'Color', 'red', 'LineStyle','-'); %media
        path ='figure/Alg1/Cloud_Num.png';
    elseif ( strcmp( type,'Alg2') )
        yline(27.9230462946061, 'Color', 'red', 'LineStyle','-'); %media
        path ='figure/Alg2/Cloud_Num.png';
    end
    lgd=legend('numero di task', 'numero medio teorico');
    lgd.Title.String = "CLOUD";
    ylabel('Numero medio di task');
    %xlabel ('numero di batch');
    saveas(stamp3,path);
    
elseif(contains(directory,'Throughput'))
    if ( strcmp( type,'Alg1') )
        yline(5.86739084706962, 'Color', 'red', 'LineStyle','-'); %media
        path = 'figure/Alg1/Cloud_Throughput.png';
    elseif ( strcmp( type,'Alg2') )
        yline(6.16278988749147, 'Color', 'red', 'LineStyle','-'); %media
        path = 'figure/Alg2/Cloud_Throughput.png';
    end
    lgd=legend('Throughput medio', 'Throughput teorico');
    lgd.Title.String = "CLOUD";
    ylabel('Throughput medio');
    saveas(stamp3,path);
    
end

%% PLOT CLOUD_TASK1

stamp4 =figure('Name','Cloud_task1');
errorbar(X, cloud_task1(:,1), cloud_task1(:,2), 'blackx');xlim([0,j+1]);
if(contains(directory,'Tempi') )
    if ( strcmp( type,'Alg1') )
        yline(4, 'Color', 'red', 'LineStyle','-'); %media
        path ='figure/Alg1/Cloud_time_task1.png';
    elseif ( strcmp( type,'Alg2') )
        yline(4, 'Color', 'red', 'LineStyle','-'); %media
        path = 'figure/Alg2/Cloud_time_task1.png';
    end
    lgd=legend('tempi medi simulati', 'tempo medio teorico');
    lgd.Title.String = "CLOUD TASK1";
    ylabel('Tempo medio di risposta');
    %xlabel ('numero di batch');
    saveas(stamp4,path);
    
elseif(contains(directory,'Task'))
    if ( strcmp( type,'Alg1') )
        yline(11.4952963534425, 'Color', 'red', 'LineStyle','-'); %media
        path = 'figure/Alg1/Cloud_Num_task1.png' ;
    elseif ( strcmp( type,'Alg2') )
        yline(0.657323422604056, 'Color', 'red', 'LineStyle','-'); %media
        path = 'figure/Alg2/Cloud_Num_task1.png' ;
    end
    lgd=legend('numero di task', 'numero medio teorico');
    lgd.Title.String = "CLOUD TASK1";
    ylabel('Numero medio di task');
    %xlabel ('numero di batch');
    saveas(stamp4,path);
    
elseif(contains(directory,'Throughput'))
    if ( strcmp( type,'Alg1') )
        yline(2.87382408836063, 'Color', 'red', 'LineStyle','-'); %media
        path = 'figure/Alg1/Cloud_Throughput_task1.png';
    elseif ( strcmp( type,'Alg2') )
        yline(0.164330855651014, 'Color', 'red', 'LineStyle','-'); %media
        path = 'figure/Alg2/Cloud_Throughput_task1.png';
    end
    lgd=legend('Throughput medio', 'Throughput teorico');
    lgd.Title.String = "CLOUD TASK1";
    ylabel('Throughput medio');
    saveas(stamp4,path);
end

%% PLOT CLOUD_TASK2
stamp5 = figure('Name','Cloud_task2');
errorbar(X, cloud_task2(:,1), cloud_task2(:,2), 'blackx');xlim([0,j+1]);
if(contains(directory,'Tempi') )
    if ( strcmp( type,'Alg1') )
        yline(4.54545454545455, 'Color', 'red', 'LineStyle','-'); %media
        path ='figure/Alg1/Cloud_time_task2.png';
    elseif ( strcmp( type,'Alg2') )
        yline(4.54545454545455, 'Color', 'red', 'LineStyle','-'); %media
        path ='figure/Alg2/Cloud_time_task2.png';
    end
    lgd=legend('tempi medi simulati', 'tempo medio teorico');
    lgd.Title.String = "CLOUD TASK2";
    ylabel('Tempo medio di risposta');
    %xlabel ('numero di batch');
    saveas(stamp5,path);
    
elseif(contains(directory,'Task'))
    if ( strcmp( type,'Alg1') )
        yline(13.6071216304954, 'Color', 'red', 'LineStyle','-'); %media
        path ='figure/Alg1/Cloud_Num_task2.png';
    elseif ( strcmp( type,'Alg2') )
        yline(27.2657228720021, 'Color', 'red', 'LineStyle','-'); %media
        path ='figure/Alg2/Cloud_Num_task2.png';
    end
    lgd=legend('numero di task', 'numero medio teorico');
    lgd.Title.String = "CLOUD TASK2";
    ylabel('Numero medio di task');
    %xlabel ('numero di batch');
    saveas(stamp5,path);
    
elseif(contains(directory,'Throughput'))
    if ( strcmp( type,'Alg1') )
        yline(2.99356675870899, 'Color', 'red', 'LineStyle','-'); %media
        path ='figure/Alg1/Cloud_Throughput_task2.png';
    elseif ( strcmp( type,'Alg2') )
        yline(5.99845903184046, 'Color', 'red', 'LineStyle','-'); %media
        path ='figure/Alg2/Cloud_Throughput_task2.png';
    end
    lgd=legend('Throughput medio', 'Throughput teorico');
    lgd.Title.String = "CLOUD TASK2";
    ylabel('Throughput medio');
    saveas(stamp5,path);
    
end

%% PLOT SYSTEM
stamp6 = figure('Name','System');
errorbar(X, system(:,1), system(:,2), 'blackx');xlim([0,j+1]);
if(contains(directory,'Tempi') )
    if ( strcmp( type,'Alg1') )
        yline(3.60084403444834, 'Color', 'red', 'LineStyle','-'); %media
        path ='figure/Alg1/System_time.png';
    elseif ( strcmp( type,'Alg2') )
        yline(3.41410882972414, 'Color', 'red', 'LineStyle','-'); %media
        path ='figure/Alg2/System_time.png';
    end
    lgd=legend('tempi medi simulati', 'tempo medio teorico');
    lgd.Title.String = "SYSTEM";
    ylabel('Tempo medio di risposta');
    %xlabel ('numero di batch');
    saveas(stamp6,path);
    
elseif(contains(directory,'Task'))
    if ( strcmp( type,'Alg1') )
        yline(44.1103394219921, 'Color', 'red', 'LineStyle','-'); %media
        path ='figure/Alg1/System_Num.png';
    elseif ( strcmp( type,'Alg2') )
        yline(41.8228331641207, 'Color', 'red', 'LineStyle','-'); %media
        path ='figure/Alg2/System_Num.png';
    end
    lgd=legend('numero di task', 'numero medio teorico');
    lgd.Title.String = "SYSTEM";
    ylabel('Numero medio di task');
    %xlabel ('numero di batch');
    saveas(stamp6,path);
    
elseif(contains(directory,'Throughput'))
    if ( strcmp( type,'Alg1') )
        yline(12.25, 'Color', 'red', 'LineStyle','-'); %media
        path ='figure/Alg1/System_Throughput.png';
    elseif ( strcmp( type,'Alg2') )
        yline(12.25, 'Color', 'red', 'LineStyle','-'); %media
        path ='figure/Alg2/System_Throughput.png';
    end
    lgd=legend('Throughput medio', 'Throughput teorico');
    lgd.Title.String = "SYSTEM";
    ylabel('Throughput medio');
    saveas(stamp6,path);
end

%% PLOT SYSTEM_TASK1

stamp7 = figure('Name','System_task1');
errorbar(X, system_task1(:,1), system_task1(:,2), 'blackx');xlim([0,j+1]);
if(contains(directory,'Tempi') )
    if ( strcmp( type,'Alg1') )
        yline(3.07372565581056, 'Color', 'red', 'LineStyle','-'); %media
        path ='figure/Alg1/System_time_task1.png';
    elseif ( strcmp( type,'Alg2') )
        yline(2.27091284611882, 'Color', 'red', 'LineStyle','-'); %media
        path ='figure/Alg2/System_time_task1.png';
    end
    lgd=legend('tempi medi simulati', 'tempo medio teorico');
    lgd.Title.String = "SYSTEM TASK1";
    ylabel('Tempo medio di risposta');
    %xlabel ('numero di batch');
    saveas(stamp7,path);
    
elseif(contains(directory,'Task'))
    if ( strcmp( type,'Alg1') )
        yline(18.4423539348633, 'Color', 'red', 'LineStyle','-'); %media
        path ='figure/Alg1/System_num_task1.png';
    elseif ( strcmp( type,'Alg2') )
        yline(13.6254770767129, 'Color', 'red', 'LineStyle','-'); %media
        path ='figure/Alg2/System_num_task1.png';
    end
    lgd=legend('numero di task', 'numero medio teorico');
    lgd.Title.String = "SYSTEM TASK1";
    ylabel('Numero medio di task');
    %xlabel ('numero di batch');
    saveas(stamp7,path);
    
elseif(contains(directory,'Throughput'))
    if ( strcmp( type,'Alg1') )
        yline(6, 'Color', 'red', 'LineStyle','-'); %media
        path ='figure/Alg1/System_Throughput_task1.png';
    elseif ( strcmp( type,'Alg2') )
        yline(6, 'Color', 'red', 'LineStyle','-'); %media
        path ='figure/Alg2/System_Throughput_task1.png';
    end
    lgd=legend('Throughput medio', 'Throughput teorico');
    lgd.Title.String = "SYSTEM TASK1";
    ylabel('Throughput medio');
    saveas(stamp7,path);
end

%% PLOT SYSTEM_TASK2
stamp8 = figure('Name','System_task2');
errorbar(X, system_task2(:,1), system_task2(:,2), 'blackx');xlim([0,j+1]);
if(contains(directory,'Tempi') )
    if ( strcmp( type,'Alg1') )
        yline(4.1068776779406, 'Color', 'red', 'LineStyle','-'); %media
        path ='figure/Alg1/System_time_task2.png';
    elseif ( strcmp( type,'Alg2') )
        yline(4.51157697398525, 'Color', 'red', 'LineStyle','-'); %media
        path ='figure/Alg2/System_time_task2.png';
    end
    lgd=legend('tempi medi simulati', 'tempo medio teorico');
    lgd.Title.String = "SYSTEM TASK2";
    ylabel('Tempo medio di risposta');
    %xlabel ('numero di batch');
    saveas(stamp8,path);
    
elseif(contains(directory,'Task'))
    if ( strcmp( type,'Alg1') )
        yline(25.6679854871288, 'Color', 'red', 'LineStyle','-'); %media
        path ='figure/Alg1/System_num_task2.png';
    elseif ( strcmp( type,'Alg2') )
        yline(28.1973560874078, 'Color', 'red', 'LineStyle','-'); %media
        path ='figure/Alg2/System_num_task2.png';
    end
    lgd=legend('numero di task', 'numero medio teorico');
    lgd.Title.String = "SYSTEM TASK2";
    ylabel('Numero medio di task');
    %xlabel ('numero di batch');
    saveas(stamp8,path);
    
elseif(contains(directory,'Throughput'))
    if ( strcmp( type,'Alg1') )
        yline(6.25, 'Color', 'red', 'LineStyle','-'); %media
        path ='figure/Alg1/System_Throughput_task2.png';
    elseif ( strcmp( type,'Alg2') )
        yline(6.25, 'Color', 'red', 'LineStyle','-'); %media
        path ='figure/Alg2/System_Throughput_task2.png';
    end
    lgd=legend('Throughput medio', 'Throughput teorico');
    lgd.Title.String = "SYSTEM TASK2";
    ylabel('Throughput medio');
    saveas(stamp8,path);
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

