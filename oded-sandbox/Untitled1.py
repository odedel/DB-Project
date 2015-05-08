# -*- coding: utf-8 -*-
# <nbformat>3.0</nbformat>

# <codecell>

class Country(object):
    def __init__(self, entity):
        self.entity = entity
        self.name = None
        self.facts = []
        self.cities = []
        
    def __repr__(self):
        return '<Country %s>' % self.entity

# <codecell>

countries = {}

# <codecell>

# Create the first 10 countries

i = 0
with open(r'C:\Users\Oded\Downloads\yagoTypes.tsv\yagoTypes.tsv') as f:
    line = f.readline()  # Skipping first line
    line = f.readline()
    while line:
        id_, entity, relation_type, super_entity = line.split()
        if super_entity == '<wikicat_Countries>':
            countries[entity] = Country(entity)
            i += 1
        line = f.readline()
        if i == 10:
            line = None

# <codecell>

# Find country name

countries_without_name = len(countries)

with open(r'C:\Users\Oded\Downloads\yagoLabels.tsv\yagoLabels.tsv') as f:
    line = f.readline()
    line = f.readline()
    while line and countries_without_name:
        id_, entity, relation_type, label = line.split('\t')[:-1]
        if relation_type == 'skos:prefLabel' and entity in countries:
            countries[entity].name = label
            countries_without_name -= 1
            print label
        line = f.readline()

# <codecell>

# Load countries with facts

facts_files = [r'C:\Users\Oded\Downloads\yagoDateFacts.tsv\yagoDateFacts.tsv', 
               r'C:\Users\Oded\Downloads\yagoFacts.tsv\yagoFacts.tsv', 
               r'C:\Users\Oded\Downloads\yagoLiteralFacts.tsv\yagoLiteralFacts.tsv']

fact_files = [r'C:\Users\Oded\Downloads\yagoFacts.tsv\yagoFacts.tsv']

for file_ in facts_files:
    with open(file_) as f:
        line = f.readline()
        line = f.readline()
        while line:
            id_, entity_first, relation, entity_second = line.split('\t', 3)
            entity_second = entity_second.rstrip()
            
            if entity_first in countries:
                countries[entity_first].facts.append(line)
                
            if entity_second in countries:
                countries[entity_second].facts.append(line)
                
            line = f.readline()

# <codecell>

# find relation types

relations = set()

for fact in c.facts:
    relations.add(fact.split(None, 3)[-2])

# <codecell>

for fact in c.facts:
    if fact.split(None, 3)[-2] == '<isLocatedIn>':
        print fact

# <codecell>

relations

# <codecell>


