using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

    class Okruh
    {
        public string Nazov { get; set; } = "";
        public long Milliseconds { get; set; }

        [System.Text.Json.Serialization.JsonIgnore]
        public TimeSpan Cas
        {
            get => TimeSpan.FromMilliseconds(Milliseconds);
            set => Milliseconds = (long)value.TotalMilliseconds;
        }

        public override string ToString() => $"{Nazov}: {Cas:hh\\:mm\\:ss\\.fff}";
    
    }

