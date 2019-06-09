function transientCompletePlot()

%apro la cartella seed
dinfo = dir(fullfile('transient'));
dinfo([dinfo.isdir]) = [];     %get rid of all directories including . and ..
nfile = length(dinfo); %numero di file attesi

request = input("Quale algoritmo vuoi plottare? (1/2):  ");
if(request == 1)
    type = "Alg1";
elseif(request == 2)
    type = "Alg2";
end
i=1;


while  (i<=nfile)
    %seleziono i files per il calcolo dell'intervallo di confidenza
    if ( contains(dinfo(i).name,type) )
        
        filename = fullfile('transient', dinfo(i).name);
        temp = importTranArea(filename);
        
        %% CLOUDLET
        cloudlet = figure(1);
        hold on
        figure(1), plot(temp.current, temp.cloudlet, '-', 'DisplayName', dinfo(i).name );
        if(i==nfile)
            %media
            if (strcmp( type,'Alg1'))
                yline(2.97808012093726, 'Color', 'black', 'LineStyle','-', 'DisplayName', 'media analitica');
            elseif(request == 2)
                yline(2.28344128305216, 'Color', 'black', 'LineStyle','-', 'DisplayName', 'media analitica');
            end
        end
        legend show
        xlabel ('Istante di tempo (s)');
        ylabel ('Tempo di risposta medio(s)');
        title('CLOUDLET AREA');
        hold on
        
        %% CLOUD
        cloud = figure(2);
        figure(2), plot(temp.current, temp.cloud, '-','DisplayName', dinfo(i).name );
        if(i==nfile)
            %media
            if (strcmp( type,'Alg1'))
                yline(4.27829313543599, 'Color', 'black', 'LineStyle','-', 'DisplayName', 'media analitica');
            elseif(request == 2)
                yline(4.53090999439737, 'Color', 'black', 'LineStyle','-', 'DisplayName', 'media analitica');
            end
        end
        legend show
        xlabel ('Istante di tempo (s)');
        ylabel ('Tempo di risposta medio(s)');
        title('CLOUD AREA');
        hold on
        
        %% SYSTEM
        system = figure(3);
        hold on
        figure(3),plot(temp.current, temp.system, '-','DisplayName', dinfo(i).name );
        if(i==nfile)
            %media
            if (strcmp( type,'Alg1'))
                yline(3.60084403444834, 'Color', 'black', 'LineStyle','-', 'DisplayName', 'media analitica');
            elseif(request == 2)
                yline(3.41410882972414, 'Color', 'black', 'LineStyle','-', 'DisplayName', 'media analitica');
            end
        end
        legend show
        xlabel ('Istante di tempo (s)');
        ylabel ('Tempo di risposta medio(s)');
        title('SYSTEM AREA');
        
    end
    i=i+1;

end


saveas(cloudlet,'figure/CloudletStazionario.png');
hold off
saveas(cloud,'figure/CloudStazionario.png');
hold off
saveas(system,'figure/SystemStazionario.png');
hold off
end

%Importfile format
function Areatable = importTranArea(filename, startRow, endRow)
%Initialize variables.
delimiter = ';';
if nargin<=2
    startRow = 2;
    endRow = inf;
end

% Read columns of data as text:
formatSpec = '%s%s%s%s%[^\n\r]';
%Open the text file.
fileID = fopen(filename,'r');
%Read columns of data according to the format.
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

% Convert the contents of columns containing numeric text to numbers.
raw = repmat({''},length(dataArray{1}),length(dataArray)-1);
for col=1:length(dataArray)-1
    raw(1:length(dataArray{col}),col) = mat2cell(dataArray{col}, ones(length(dataArray{col}), 1));
end
numericData = NaN(size(dataArray{1},1),size(dataArray,2));

for col=[1,2,3,4]
    % Converts text in the input cell array to numbers. Replaced non-numeric
    % text with NaN.
    rawData = dataArray{col};
    for row=1:size(rawData, 1)
        % Create a regular expression to detect and remove non-numeric prefixes and
        % suffixes.
        regexstr = '(?<prefix>.*?)(?<numbers>([-]*(\d+[\,]*)+[\.]{0,1}\d*[eEdD]{0,1}[-+]*\d*[i]{0,1})|([-]*(\d+[\,]*)*[\.]{1,1}\d+[eEdD]{0,1}[-+]*\d*[i]{0,1}))(?<suffix>.*)';
        try
            result = regexp(rawData(row), regexstr, 'names');
            numbers = result.numbers;
            
            % Detected commas in non-thousand locations.
            invalidThousandsSeparator = false;
            if numbers.contains(',')
                thousandsRegExp = '^[-/+]*\d+?(\,\d{3})*\.{0,1}\d*$';
                if isempty(regexp(numbers, thousandsRegExp, 'once'))
                    numbers = NaN;
                    invalidThousandsSeparator = true;
                end
            end
            % Convert numeric text to numbers.
            if ~invalidThousandsSeparator
                numbers = textscan(char(strrep(numbers, ',', '')), '%f');
                numericData(row, col) = numbers{1};
                raw{row, col} = numbers{1};
            end
        catch
            raw{row, col} = rawData{row};
        end
    end
end
% Replace non-numeric cells with 0.0
R = cellfun(@(x) (~isnumeric(x) && ~islogical(x)) || isnan(x),raw); % Find non-numeric cells
raw(R) = {0.0}; % Replace non-numeric cells

% Create output variable
Areatable = table;
Areatable.current = cell2mat(raw(:, 1));
Areatable.cloudlet = cell2mat(raw(:, 2));
Areatable.cloud = cell2mat(raw(:, 3));
Areatable.system = cell2mat(raw(:, 4));
end