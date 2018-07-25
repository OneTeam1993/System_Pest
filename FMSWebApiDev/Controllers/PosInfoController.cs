﻿using FMSWebApi.Models;
using FMSWebApi.Repository;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;
using System.Web.Http.Cors;

namespace FMSWebApi.Controllers
{
    [EnableCors(origins: "*", headers: "*", methods: "*")]
    public class PosInfoController : ApiController
    {
        private static readonly IPosRepository repository = new PosInfoRepository();

        // GET: api/PosInfo
        public IEnumerable<PosInfo> GetPosFiltered([FromUri]PosInfo param)
        {
            if ((param.Timestamp != DateTime.MinValue && param.RxTime != DateTime.MinValue) &&
                (param.CompanyID > 0 || param.AssetID > 0 || !string.IsNullOrEmpty(param.Asset)))
            {           
                return repository.Get(param);
            }
            else
            {
                return repository.GetAll();
            }
        }

        // GET: api/PosInfo/5
        public PosInfo Get(int id)
        {
            PosInfo currPos = repository.Get(id);
            if (currPos == null)
            {
                throw new HttpResponseException(HttpStatusCode.NotFound);
            }
            return currPos;
        }
        

        // POST: api/PosInfo
        public PosInfo Post([FromBody]PosInfo value)
        {
            value = repository.Add(value);
            return value;
        }

        // PUT: api/PosInfo/5
        public void Put(int id, [FromBody]PosInfo value)
        {
            value.AssetID = id;
            if (!repository.Update(value))
            {
                throw new HttpResponseException(HttpStatusCode.NotFound);
            }
        }

        // DELETE: api/PosInfo/5
        public void Delete(int id)
        {
            PosInfo currAsset = repository.Get(id);
            if (currAsset == null)
            {
                throw new HttpResponseException(HttpStatusCode.NotFound);
            }
            repository.Remove(id);
        }
    }
}
