function seedTest()

%apro la cartella seed
dinfo = dir(fullfile('seeds'));
dinfo([dinfo.isdir]) = [];     %get rid of all directories including . and ..
nfiles = length(dinfo);

%inizializzo la matrice di celle
dataset = cell(nfiles,1);

%importo i files dei seeds
for j = 1 : nfiles
    filename = fullfile('seeds', dinfo(j).name);
    
    dataset{j,1} = dinfo(j).name; %concatena il nome del file
    dataset{j,2} = importSeed(filename); %concatena i valori del file
end
disp(dataset);

%Il "Wilcoxon ranksum Test", un test non parametrico che valuta l'indipendenza di due samples


for i=1: nfiles-1
    [p,h,stats] = ranksum(dataset{i,2},dataset{i+1,2});
    if(h==0)
        fprintf("I due dataset %s e %s sono indipendenti e identicamente distribuiti con uguale media\n con una accuratezza del %f.\n",dataset{i,1}, dataset{i+1,1}, p);
    else
        fprintf("I due dataset %s e %s non sono indipendenti e identicamente distribuiti.\n", dataset{i,1},dataset{i+1,1});
    end
end
end
