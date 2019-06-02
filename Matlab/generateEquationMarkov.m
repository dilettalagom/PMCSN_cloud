function generateEquationMarkov()

n = 21;

la = 6.00;
lb = 6.25;
ma = 0.45;
mb = 0.27;

mAc= 0.25;
mBc = 0.22;

dim = zeros(1,(n)*(n+1)/2);
states = string(dim);
equations = sym(dim);
count= 1;

%Costruzione delle equazioni
for layers = 1:n
    i = layers - 1;
    j = 0;
    for k = 1:layers
        
        if i == 0
            b1 = 0;
        else
            b1 = 1;
        end
        if j == 0
            b2 = 0;
        else
            b2 = 1;
        end
        if layers == n
            a = 0;
        else
            a = 1;
        end
        
        
        
        str = 'p'+string(i)+'o'+string(j);
        str = sym(str);
        if ( a ~= 0 )
            str1 = 'p'+string(i+1)+'o'+string(j);
            str2 = 'p'+string(i)+'o'+string(j+1);
            str1 = sym(str1);
            str2 = sym(str2);
            
        else
            str1 = sym('a');
            str2 = sym('a');
        end
        if ( b1 ~= 0 )
            str3 = 'p'+string(i-1)+'o'+string(j);
            str3 = sym(str3);
        else
            str3 = sym('a');
        end
        if ( b2 ~= 0 )
            str4 = 'p'+string(i)+'o'+string(j-1);
            str4 = sym(str4);
        else
            str4 = sym('a');
        end
        
        equations(count) = sym (str*(a*la + a*lb + i*ma + j*mb) - a*str1*(i + 1)*ma - a*str2*(j + 1)*mb - b1*str3*la - b2*str4*lb == 0);
        
        states(count)= str;
        
        
        count=count+1;
        i=i-1;
        j=j+1;
    end
end
states = sym(states);


%%

%Sostituzione dell' equazione degli stati che sommano a 1 all'ultima
%equazione

count=count-1;
equations(count) = states(1);
for i = 2:count
    equations(count) = equations(count) + states(i) ;
end
equations(count) = equations(count) - 1== 0;

%disp(x);

%%

%Risoluzione delle equazioni
format long g;
[A,b] = equationsToMatrix(equations,states);

X = linsolve(A,b);
Y = double(X);


%%
%somma delle probabilità degli stati dell'ultimo layer
l= length(Y);
u= l-20;
pq = sum( Y( u:l ) );
disp("pq generato analiticamente: "+pq);

%%
s = 0;
s1 = 0;
count= 2;
for layers = 2:n
    for k = 1:layers
        s = s + (layers-1)*Y(count+k-1);  % somma( n*p(i,j))
        s1 = s1 + (layers-1)*Y(count+k-1)
    end
    count=count+k;
end

Rclet = s / 12.25; % cloudlet
disp("E[T]_CLOUDLET generato analiticamente dalla catena di Markov: " + Rclet);

Rc = pq*(mAc+mBc)/2; % cloud
disp("E[T]_CLOUD generato analiticamente dalla catena di Markov: " + Rc);

Rtot = Rclet+Rc; %sistema
disp("E[T]_SISTEMA generato analiticamente dalla catena di Markov: " + Rtot);


%%

Alg1 = importIntervalConfidence('IntervalloConfidenza215487963_Alg1.csv', 3);
stop = [100.0,200.0,300.0,400.0,500.0,600.0,700.0,800.0,900.0,1000.0,1100.0,1200.0,1300.0,1400.0,1500.0,1600.0];

means = 1:length(stop);
errors = 1:length(stop);



for i = 1:length(stop)
    AlgFiltered=Alg1(Alg1.stop==stop(i),:);
    %genera intervallo di confidenza ci = [x-intervallo; x+intevallo] 
    [~,~,ci,~] = ztest(AlgFiltered.system,mean(AlgFiltered.system),std(AlgFiltered.system),0.05,0);
    
    means(i) = mean(AlgFiltered.system);
    errors(i) = abs(ci(1)-ci(2));
    
end

%plot degli intervalli di confidenza e della retta == media
figure;
xlim([0 1800])
ylim([1.45 1.85])
yline(Rtot,'blue'); %plotting della media teorica
hold on
errorbar(stop,means,errors,'ro');  %plotting degli intervalli

%%
d=dir("./batch");
s=size(d);
means = 1:s(1)-2;
errors = 1:s(1)-2;
j=1:s(1)-2;
j(1)=3;
for i = 3:s(1)
    Alg1 = importBatch("./batch/"+convertCharsToStrings(d(i).name), 3);

    %genera intervallo di confidenza ci = [x-intervallo; x+intevallo] 
    [~,~,ci,~] = ztest(Alg1.system,mean(Alg1.system),std(Alg1.system),0.05,0);
    
    means(i-2) = mean(Alg1.system);
    errors(i-2) = abs(ci(1)-ci(2));
  
    j(i-2)=i;
end
%plot degli intervalli di confidenza e della retta == media
figure;
xlim([0 10])
ylim([1.65 1.68])

yline(Rtot,'blue'); %plotting della media teorica
hold on
errorbar(j,means,errors,'ro');  %plotting degli intervalli