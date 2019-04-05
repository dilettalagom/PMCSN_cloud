function [] = intervalloConfidenza()
%UNTITLED Summary of this function goes here
%   Detailed explanation goes here

Alg1 = importIntervalConfidence('215487963_Alg1.csv', 2, 121);
cat= [20,50,100,150,200,500];
y = (1.66)*ones(length(cat));

%num=str2double(cat);

means= 1:length(cat);
errors= 1:length(cat);

for i = 1:length(cat)
    AlgFiltered=Alg1(Alg1.stop==cat(i),:);
    [h,p,ci,zval] = ztest(AlgFiltered.system,mean(AlgFiltered.system),std(AlgFiltered.system),0.05,0);
    means(i)=mean(AlgFiltered.system);
    errors(i)=abs(ci(1)-ci(2));
end
yline(1.66);
hold on
errorbar(cat,means,errors,'ro');


%%

%calcolo manuale
SEM = std(V)/sqrt(length(V));               % Standard Error

ts = norminv([0.025  0.975]);               % Z-Score, passo l'intervallo di probabilità in cui
                                            %l'intervallo di confidenza per
                                            %la media
                                            %deve essere contenuto
                                            
CI = mean(V) + ts*SEM                       % Confidence Intervals

end

