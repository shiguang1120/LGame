﻿using Microsoft.Xna.Framework.Content;
using System;
using System.Collections.Generic;
using System.IO;
using System.Text;

namespace loon.monogame
{
    public class MonoGameContentManager : ContentManager
    {
        public MonoGameContentManager(IServiceProvider serviceProvider, string rootDirectory) : base(serviceProvider, rootDirectory)
        {
        }

        public new Stream OpenStream(string assetName)
        {
            LoonFileContent fileContent = base.Load<LoonFileContent>(assetName);
            return new MemoryStream(fileContent.content);
        }
    }
}
