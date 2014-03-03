bitcoin-exchange
================

a universal, extendable Java interface for crypto exchange APIs. It's written with the intent of having minimal dependencies for specific libraries.

- slf4j / logback
- apache commons-lang3
- apache commons-math3
- javax.ws.rs-api
- javax.json-api
- junit (testing only)

It is packaged as maven project, but not submitted to maven central (yet).

The architecture is plug-in based, using the java.util.ServiceLoader infrastructure. Each exchange market is implemented as plugin as well, the framework picks up external implementations of Markets/Exchange APIs automatically.

Be advised: this is a spare time project, still rough around the edges.

Features
========

- Integration tests
- Plug-in architecture, easily extendable
- Interfaces for public market data and private trading methods
- work in progress ;)


Usage
=====

  // lookup a market
  Market bitstampMarket = Markets.getMarket("bitstamp");
  
  // the Market object provides static information like 
  // traded assets, withdrawal and deposit methods. It is
  // also a factory for the trading client
  ExchangeClient client = bitstampMarket.createClient();
  
  // the trading client implements 2 interfaces 
  // public ticker:
  TickerValue ticker = client.getTicker(new AssetPair(Currency.BTC, Currency.USD));


  // call trading api, needs auth info
  // for this to work, configuration must be setup properly, see below
  Balance myCoins =  client.getBalance();


Configuration
=============

to be able to use the "private" trading methods of the various exchanges, you are required to add your API key and secret to a file "bitcoin-exchange.properties" that must be in the classpath of the running application.

In that file add lines for each market:

  marketkey.secret=<YOUR API SECRET>
  marketkey.userid=<YOUR API KEY>

replace "marketkey" with the value of the market's getKey() method (i.e. "bitstamp" or "kraken"). Additionally most markets have no API support for retrieving deposit addresses, you can add them to the config file:

  marketkey.deposit.btc = 13NwtPR5csotp5GATWCRtPY6ZtDCVe6cu4

for Bitcoin, use for other currencies accordingly.

Attention: some markets need extra configuration, for now these are: Bitstamp, Vircurex.
- on Bitstamp: add your numeric customer id: bitstamp.customerid=<YOUR ID NUMBER>
- vircurex needs "words" as key for specific API actions, i.e. vircurex.words.balance=<YOUR BALANCE WORD> (please see source for more details)




How to extend
=============

1. implement Market class (derive from abstract at.outdated.bitcoin.exchange.api.market.Market)
2. the method getKey() must return a unique (short) string
3. create file META-INF/services/at.outdated.bitcoin.exchange.api.market.Market
3. add 1 line naming your full Market implementation class name (this is for the ServiceLoader)
4. implement the exchange client class, using the interfaces MarketClient and TradeClient 
5. You're done, happy trading!
 
 
TODO
====
-  Write a real documention. For now this is just a spare time pet project - sry.
-  more tests!
-  find a way to implement withdrawals even if they are not supported by the official APIs

thanks for reading to the end ;)

if you want to donate, please send your satoshis to this address: 13NwtPR5csotp5GATWCRtPY6ZtDCVe6cu4