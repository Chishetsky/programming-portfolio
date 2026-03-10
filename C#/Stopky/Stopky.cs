using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

    
    class Stopky
    {
        private Stopwatch stopwatch = new Stopwatch();

        public TimeSpan uplynulyCas
        {
            get { return stopwatch.Elapsed; }

        }

        public bool spustene   
        {
            get { return stopwatch.IsRunning; }
        }

        public void Start() 
        {
            stopwatch.Start();
        }

        public void Stop() 
        {
            stopwatch.Stop();
        }

        public void Reset() 
        {
            stopwatch.Reset();
        }

    }

